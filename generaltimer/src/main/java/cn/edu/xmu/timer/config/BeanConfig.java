package cn.edu.xmu.timer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 22:25
 **/
@Configuration
@EnableAsync
public class BeanConfig {

    @Value("${generaltimer.max-thread}")
    private int maxThread;

    /**
     * 定义@Scheduled的线程池
     * @return
     */
    @Bean
    public TaskExecutor jobExecutor(){
        ThreadPoolTaskExecutor jobExecutor = new ThreadPoolTaskExecutor();
        jobExecutor.setCorePoolSize(maxThread / 2);
        jobExecutor.setMaxPoolSize(maxThread);
        jobExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return jobExecutor ;
    }
}
