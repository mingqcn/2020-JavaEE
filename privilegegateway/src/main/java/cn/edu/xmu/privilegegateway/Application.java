package cn.edu.xmu.privilegegateway;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Ming Qiu
 **/
@SpringBootApplication
@EnableDubbo //开启dubbo的注解支持
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
