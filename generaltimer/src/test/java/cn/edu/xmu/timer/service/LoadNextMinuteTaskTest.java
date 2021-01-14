package cn.edu.xmu.timer.service;

import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.bo.TimeWheel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * @Author zhibin lan
 * @Date 2020/12/2
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc //配置模拟的MVC，这样可以不启动服务器测试
@Slf4j
public class LoadNextMinuteTaskTest {

    @Autowired
    private ScheduleJob scheduleJob;

    private MockMvc mvc;

    @Test
    public void loadNextMinuteTaskTest() {
        List<Task> tasks = new ArrayList<>();

        Task task = new Task();
        task.setBeanName(ScheduleJob.class.getName());
        task.setMethodName("test");
        task.setSenderName("testRocketmq");
        task.setReturnTypeName("void");
        task.setTag(null);
        task.setTopic(null);
        task.setParamList(new ArrayList<Param>());
        //设置时间
        int second = 5;
        LocalDateTime loadTime = LocalDateTime.now();
        LocalDateTime localDateTime = LocalDateTime.of(loadTime.getYear(),loadTime.getMonth(),
                loadTime.getDayOfMonth(),loadTime.getHour(),loadTime.getMinute(),second);
        task.setBeginTime(localDateTime);

        tasks.add(task);
        Map<Integer,List<Task>> map = new TreeMap<Integer,List<Task>>();
        map.put(0,tasks);
        log.debug(scheduleJob.getMinuteWheel().toString());
        scheduleJob.getMinuteWheel().getCells().set(0,tasks);
        log.debug(scheduleJob.getMinuteWheel().getCells().toString());

        log.debug(scheduleJob.getSecondWheel().getCells().toString());
        scheduleJob.loadNextMinuteTask();

        while(scheduleJob.getSecondWheel().getCells().get(30).isEmpty());

        log.debug("test thread: "+Thread.currentThread().toString());
        log.debug("int test:" + scheduleJob.getSecondWheel().getCells().toString());

        TimeWheel secondWheelTest = new TimeWheel(60,0);

        List<Task> taskList= new ArrayList<>();

        //下一次装载任务
        Task task2 = new Task();
        task2.setBeanName(ScheduleJob.class.getName());
        task2.setMethodName("loadNextMinuteTask");
        task2.setSenderName(ScheduleJob.class.getName());
        task2.setReturnTypeName("void");
        task2.setTag(null);
        task2.setTopic(null);
        task2.setParamList(new ArrayList<Param>());

        task2.setBeginTime(LocalDateTime.of(loadTime.getYear(),loadTime.getMonth(),
                loadTime.getDayOfMonth(),loadTime.getHour(),loadTime.getMinute(),0));

        List<Task> taskList1= new ArrayList<>();
        List<Task> taskList2= new ArrayList<>();
        taskList1.add(task2);

        //分钟轮上的任务
        task.setBeginTime(LocalDateTime.of(loadTime.getYear(),loadTime.getMonth(),
                loadTime.getDayOfMonth(),loadTime.getHour(),loadTime.getMinute(),5));
        taskList2.add(task);
        secondWheelTest.getCells().set(30,taskList1);
        secondWheelTest.getCells().set(35,taskList2);
        log.debug(secondWheelTest.getCells().toString());
        log.debug(scheduleJob.getMinuteWheel().getCells().toString());
        assertEquals(secondWheelTest.getCells(),scheduleJob.getSecondWheel().getCells());
    }




}
