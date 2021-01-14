package cn.edu.xmu.privilegegateway.util;

import cn.edu.xmu.privilegeservice.client.IGatewayService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * @title GatewayUtil.java
 * @description 网关工具类
 * @author wwc
 * @date 2020/12/02 17:11
 * @version 1.0
 */
@Component
@Slf4j
public class GatewayUtil {

    /**
     * dubbo远程调用
     */
    @DubboReference
    private IGatewayService iGatewayService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 供Authfilter使用的工具
     */
    public static RedisTemplate redis;

    public static IGatewayService gatewayService;

    public static Map<String, Integer> gatewayPrivList;

    @Value("${privilegegateway.jwtExpire:3600}")
    private static Integer jwtExpireTime = 3600;

    @Value("${privilegegateway.refreshJwtTime:60}")
    private static Integer refreshJwtTime = 60;

    /**
     * 存放在redis中的url权限的key
     */
    private final String urlKeyName = "Priv";

    @PostConstruct
    public void getRedisTemplate(){
        redis = this.redisTemplate;
        log.info("初始化-------redisTemplate----");
        gatewayService = this.iGatewayService;
        log.info("初始化-------GatewayService----");
        gatewayPrivList = this.redisTemplate.opsForHash().entries(urlKeyName);
        log.info("初始化-------gatewayPrivList----");
    }

    public static String getUrlPrivByKey(String urlKeyName) {
        Set<String> allKeySet = gatewayPrivList.keySet();
        if (allKeySet.contains(urlKeyName)) {
            return gatewayPrivList.get(urlKeyName).toString();
        } else {
            return null;
        }
    }

    public static Integer getJwtExpireTime() {
        return jwtExpireTime;
    }

    public static Integer getRefreshJwtTime() {
        return refreshJwtTime;
    }

    /**
     * 请求类型
     */
    public enum RequestType {
        GET(0, "GET"),
        POST(1, "POST"),
        PUT(2, "PUT"),
        DELETE(3, "DELETE");

        private static final Map<Integer, RequestType> typeMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            typeMap = new HashMap();
            for (RequestType enum1 : values()) {
                typeMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        RequestType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static RequestType getTypeByCode(Integer code) {
            return typeMap.get(code);
        }

        public static RequestType getCodeByType(HttpMethod method) {
            switch (method) {
                case GET: return RequestType.GET;
                case PUT: return RequestType.PUT;
                case POST: return RequestType.POST;
                case DELETE: return RequestType.DELETE;
                default: return null;
            }
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

    }
}
