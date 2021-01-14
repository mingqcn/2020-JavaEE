package cn.edu.xmu.privilegegateway.localfilter;

import cn.edu.xmu.privilegegateway.util.JwtHelper;
import cn.edu.xmu.privilegeservice.client.IGatewayService;
import cn.edu.xmu.privilegegateway.util.GatewayUtil;
import com.alibaba.fastjson.JSONObject;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/13 22:31
 **/
public class AuthFilter implements GatewayFilter, Ordered {
    private  static  final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private String tokenName;

    public AuthFilter(Config config){
        this.tokenName = config.getTokenName();
    }

    /**
     * gateway001 权限过滤器
     * 1. 检查JWT是否合法,以及是否过期，如果过期则需要在response的头里换发新JWT，如果不过期将旧的JWT在response的头中返回
     * 2. 判断用户的shopid是否与路径上的shopid一致（0可以不做这一检查）
     * 3. 在redis中判断用户是否有权限访问url,如果不在redis中需要通过dubbo接口load用户权限
     * 4. 需要以dubbo接口访问privilegeservice
     * @param exchange
     * @param chain
     * @return
     * @author wwc
     * @date 2020/12/02 17:13
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 获取请求参数
        String token = request.getHeaders().getFirst(tokenName);
        RequestPath url = request.getPath();
        HttpMethod method = request.getMethod();
        // 判断token是否为空，无需token的url在配置文件中设置
        logger.debug("filter: token = " + token);
        if (StringUtil.isNullOrEmpty(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.empty());
        }
        // 判断token是否合法
        JwtHelper.UserAndDepart userAndDepart = new JwtHelper().verifyTokenAndGetClaims(token);
        if (userAndDepart == null) {
            // 若token解析不合法
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.writeWith(Mono.empty());
        } else {
            // 若token合法
            // 获取redis工具
            RedisTemplate redisTemplate = GatewayUtil.redis;
            // 判断该token是否被ban
            String[] banSetName = {"BanJwt_0", "BanJwt_1"};
            for (String singleBanSetName : banSetName) {
                // 若redis有该banSetname键则检查
                if (redisTemplate.hasKey(singleBanSetName)) {
                    // 获取全部被ban的jwt,若banjwt中有该token则拦截该请求
                    if (redisTemplate.opsForSet().isMember(singleBanSetName, token)) {
                        response.setStatusCode(HttpStatus.UNAUTHORIZED);
                        return response.writeWith(Mono.empty());
                    }
                }
            }
            // 检测完了则该token有效
            // 解析userid和departid和有效期
            Long userId = userAndDepart.getUserId();
            Long departId = userAndDepart.getDepartId();
            Date expireTime = userAndDepart.getExpTime();
            // 检验api中传入token是否和departId一致
            if (url != null) {
                // 获取路径中的shopId
                Map<String, String> uriVariables = exchange.getAttribute(ServerWebExchangeUtils.URI_TEMPLATE_VARIABLES_ATTRIBUTE);;
                String pathId = uriVariables.get("shopid");
                if (pathId != null && !departId.equals(0L)) {
                    // 若非空且解析出的部门id非0则检查是否匹配
                    if (!pathId.equals(departId.toString())) {
                        // 若id不匹配
                        logger.debug("did不匹配:" + pathId);
                        response.setStatusCode(HttpStatus.FORBIDDEN);
                        return response.writeWith(Mono.empty());
                    }
                }
                logger.debug("did匹配");
            } else {
                logger.debug("请求url为空");
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return response.writeWith(Mono.empty());
            }
            String jwt = token;
            // 判断该token有效期是否还长，load用户权限需要传token，将要过期的旧的token暂未放入banjwt中，有重复登录的问题
            Long sec = expireTime.getTime() - System.currentTimeMillis();
            if (sec < GatewayUtil.getRefreshJwtTime() * 1000) {
                // 若快要过期了则重新换发token
                // 创建新的token
                JwtHelper jwtHelper = new JwtHelper();
                jwt = jwtHelper.createToken(userId, departId, GatewayUtil.getJwtExpireTime());
                logger.debug("重新换发token:" + jwt);
            }
            // 判断redis中是否存在该用户的token，若不存在则重新load用户的权限
            String key = "up_" + userId;
            if (!redisTemplate.hasKey(key)) {
                // 如果redis中没有该键值
                // 通过内部调用将权限载入redis并返回新的token
                IGatewayService iGatewayService = GatewayUtil.gatewayService;
                iGatewayService.loadSingleUserPriv(userId, jwt);
            }
            // 将token放在返回消息头中
            response.getHeaders().set(tokenName, jwt);
            // 将url中的数字替换成{id}
            Pattern p = Pattern.compile("/(0|[1-9][0-9]*)");
            Matcher matcher = p.matcher(url.toString());
            String commonUrl = matcher.replaceAll("/{id}");
            logger.debug("获取通用请求路径:" + commonUrl);
            // 找到该url所需要的权限id
            String urlKey = commonUrl + "-" + GatewayUtil.RequestType.getCodeByType(method).getCode().toString();
            String privKey = GatewayUtil.getUrlPrivByKey(urlKey);
            if (privKey == null || privKey.isEmpty()) {
                // 若该url无对应权限id
                logger.debug("该url无权限id:" + urlKey);
                return chain.filter(exchange);
            }
            // 拿到该用户的权限位,检验是否具有访问该url的权限
            if (redisTemplate.opsForSet().isMember(key, Integer.valueOf(privKey))) {
                return chain.filter(exchange);
            }
            // 若全部检查完则无该url权限
            logger.debug("无权限");
            // 设置返回消息
            JSONObject message = new JSONObject();
            message.put("errno", 403);
            message.put("errmsg", "无权限访问该url");
            byte[] bits = message.toJSONString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            //指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.writeWith(Mono.just(buffer));
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    public static class Config {
        private String tokenName;

        public Config(){

        }

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }
    }
}
