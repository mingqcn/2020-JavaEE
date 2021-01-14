package cn.edu.xmu.ooad;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author Ming Qiu
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class PublicTestApp {
    public static void main(String[] args) {
        SpringApplication.run(PublicTestApp.class, args);
    }
}
