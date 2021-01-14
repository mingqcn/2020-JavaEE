package cn.edu.xmu.timer.service;

import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.bo.TimeWheel;
import io.lettuce.core.StrAlgoArgs;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @Author Li Zihan
 * @Date 2020/12/2
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AddTaskTest {

    @Autowired
    private ScheduleJob scheduleJob;

    @Test
    public void loadNextMinuteTaskTest() {
        Vector<List<Task>> listVector = new Vector<>(120);
        List<Task> tasks = new ArrayList<>();

        Task task = new Task();
        task.setBeanName(ScheduleJob.class.getName());
        task.setMethodName("test");
        task.setReturnTypeName("void");
        task.setTag(null);
        task.setTopic(null);
        Byte a=0;
        task.setPeriod(a);
        //task.setParamList(new ArrayList<Param>());
        //设置时间
        int second = 5;
        int hour=0;
        int minute=0;
        LocalDateTime loadTime = LocalDateTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(loadTime.getYear(),loadTime.getMonth(),
                loadTime.getDayOfMonth(),loadTime.getHour(),loadTime.getMinute()+10,5);
        task.setSendTime(localDateTime);

        tasks.add(task);

        log.info(scheduleJob.getMinuteWheel().toString());
        log.info(scheduleJob.getMinuteWheel().getCells().toString());
        log.info(scheduleJob.getSecondWheel().getCells().toString());
        scheduleJob.addJob(tasks);
        //while(scheduleJob.getSecondWheel().getCells().get(30).isEmpty());

        log.info("test thread: "+Thread.currentThread().toString());
        log.info("int test:" + scheduleJob.getHourWheel().getCells().toString());
    }
}
