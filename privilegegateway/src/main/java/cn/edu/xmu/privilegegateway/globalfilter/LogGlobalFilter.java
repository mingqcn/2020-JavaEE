package cn.edu.xmu.privilegegateway.globalfilter;


import cn.edu.xmu.privilegegateway.util.JacksonUtil;
import cn.edu.xmu.privilegegateway.util.JwtHelper;
import cn.edu.xmu.privilegegateway.model.Log;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;


import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/13 22:19
 **/
@Component
public class LogGlobalFilter implements GlobalFilter, Ordered {
    private  static  final Logger logger = LoggerFactory.getLogger(LogGlobalFilter.class);
    @Resource
    RocketMQTemplate rocketMQTemplate;
    @Autowired
    RedisTemplate redisTemplate;

    JwtHelper jwtHelper = new JwtHelper();

    HashMap<HttpMethod, String> methodMap = new HashMap<>(){
        {
            put(HttpMethod.GET, "0");
            put(HttpMethod.POST, "1");
            put(HttpMethod.PUT, "2");
            put(HttpMethod.DELETE, "3");
        }
    };

    /**
     * gatway002 日志过滤器
     * 需要利用RocketMQ将日志写入数据库，log-topic
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 请求进入之前记录到达时间
        LocalDateTime startTime = LocalDateTime.now();
        UUID uuid = UUID.randomUUID();

        Log log = processLog(exchange.getRequest());
        log.setBeginTime(startTime);
        log.setUuid(uuid.toString());
        sendLog(log);

        return chain.filter(exchange).then(Mono.fromRunnable(()->{
            LocalDateTime endTime = LocalDateTime.now();

            /* 局域过滤器有可能执行了令牌换发，因此重新获得令牌 */
            if(log.getUserId().equals(-1) || log.getDepartId().equals(-1)){
                String token =  exchange.getRequest().getHeaders().getFirst("authorization");
                JwtHelper.UserAndDepart u = jwtHelper.verifyTokenAndGetClaims(token);
                if(u == null){
                    log.setUserId(-1L);
                    log.setDepartId(-1L);
                } else {
                    log.setUserId(u.getUserId());
                    log.setDepartId(u.getDepartId());
                }
            }

            log.setEndTime(endTime);
            log.setSuccess(exchange.getResponse().getStatusCode().is2xxSuccessful() ? (byte)1 :(byte)0);

            sendLog(log);
        }));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Log processLog(ServerHttpRequest serverHttpRequest){
        String token =  serverHttpRequest.getHeaders().getFirst("authorization");
        JwtHelper.UserAndDepart u = jwtHelper.verifyTokenAndGetClaims(token);

        String uri = serverHttpRequest.getURI().getRawPath();
        /* 将url中的数字替换成{id} */
        Pattern p = Pattern.compile("/(0|[1-9][0-9]*)");
        Matcher matcher = p.matcher(uri);
        String commonUri = matcher.replaceAll("/{id}");

        HttpMethod httpMethod = serverHttpRequest.getMethod();
        logger.debug(commonUri + '-' + methodMap.get(httpMethod));
        Integer privilegeId = 1;//(Integer) redisTemplate.boundHashOps("Priv").get(commonUri + '-' + methodMap.get(httpMethod));

        Log log = new Log();
        privilegeId = privilegeId == null ? -1 : privilegeId; // 如果Redis中没有关于此URL的记录则赋值-1
        if(u == null){
            log.setUserId(-1L);
            log.setDepartId(-1L);
        } else {
            log.setUserId(u.getUserId());
            log.setDepartId(u.getDepartId());
        }
        log.setIp(serverHttpRequest.getRemoteAddress().toString());
        log.setDesc("visited:" + serverHttpRequest.getURI().getRawPath());
        log.setGmtCreate(LocalDateTime.now());
        log.setPrivilegeId(privilegeId.longValue());

        return log;
    }


    /**
     * 向RocketMQ发送消息
     * @param log
     */
    private void sendLog(Log log){
        String json = JacksonUtil.toJson(log);
        Message message = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay("log-topic", message);
        logger.debug(json);
    }
}
