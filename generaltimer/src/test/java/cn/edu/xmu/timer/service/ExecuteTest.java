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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * 2 * @author: LiangJi3229
 * 3 * @date: 2020/12/3 下午12:32
 * 4
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class ExecuteTest {
    @Autowired
    ScheduleJob scheduleJob;
    @Test
    void testSecondWheelTimeBeforeNow() throws InterruptedException {
        //if(LocalDateTime.now().getSecond()>40)return;
        TimeWheel secondWheel=new TimeWheel(60,(LocalDateTime.now().getSecond()+2)%60);
        scheduleJob.setSecondWheel(secondWheel);
        //while(LocalDateTime.now().getSecond()>=3);
        Task task1=new Task();
        task1.setBeanName("testService");
        task1.setMethodName("test");
        task1.setReturnTypeName("void");
        task1.setTag(null);
        task1.setTopic(null);
        task1.setParamList(new ArrayList<Param>());
        LocalDateTime now=LocalDateTime.now();

        task1.setBeginTime(now.plusSeconds(2));
        System.out.println("task1 beginTime:"+task1.getBeginTime());
        secondWheel.getCells().get((now.getSecond()+2)%60).add(task1);
        Task task2=new Task();
        task2.setBeanName("testService");
        task2.setMethodName("test");
        task2.setReturnTypeName("void");
        task2.setTag(null);
        task2.setTopic(null);
        task2.setParamList(new ArrayList<Param>());
        task2.setBeginTime(now.plusSeconds(6));
        System.out.println("task2 beginTime:"+task2.getBeginTime());
        secondWheel.getCells().get((now.getSecond()+6)%60).add(task2);
        Thread.sleep(10100);
        //这里后加prepareTime
        assertEquals(scheduleJob.getSecondWheel().getCurrent(),(LocalDateTime.now().getSecond()+10+1)%60);
    }
    @Test
    void testSecondWheelTimeAfterNow() throws InterruptedException {
        //if(LocalDateTime.now().getSecond()>40)return;
        TimeWheel secondWheel=new TimeWheel(60,(LocalDateTime.now().getSecond()+14)%60);
        scheduleJob.setSecondWheel(secondWheel);
        System.out.println("current="+scheduleJob.getSecondWheel().getCurrent());
        //while(LocalDateTime.now().getSecond()>=3);
        Task task1=new Task();
        task1.setBeanName("testService");
        task1.setMethodName("test");
        task1.setReturnTypeName("void");
        task1.setTag(null);
        task1.setTopic(null);
        task1.setParamList(new ArrayList<Param>());
        LocalDateTime now=LocalDateTime.now();

        task1.setBeginTime(now.plusSeconds(14));
        System.out.println("task1 beginTime:"+task1.getBeginTime()+" onWheel:"+(now.getSecond()+14));
        secondWheel.getCells().get((now.getSecond()+14)%60).add(task1);
        Task task2=new Task();
        task2.setBeanName("testService");
        task2.setMethodName("test");
        task2.setReturnTypeName("void");
        task2.setTag(null);
        task2.setTopic(null);
        task2.setParamList(new ArrayList<Param>());
        task2.setBeginTime(now.plusSeconds(16));
        System.out.println("task2 beginTime:"+task2.getBeginTime()+" onWheel:"+(now.getSecond()+16));
        secondWheel.getCells().get((now.getSecond()+16)%60).add(task2);
        Thread.sleep(9100);
        //这里后加prepareTime
        assertEquals(scheduleJob.getSecondWheel().getCurrent(),(LocalDateTime.now().getSecond()+10+1)%60);
    }
    @Test
    void testSecondWheelTimeAfterNowMoreThanHalf() throws InterruptedException {
        //if(LocalDateTime.now().getSecond()>40)return;
        TimeWheel secondWheel=new TimeWheel(60,(LocalDateTime.now().getSecond()+41)%60);
        scheduleJob.setSecondWheel(secondWheel);
        System.out.println("current="+scheduleJob.getSecondWheel().getCurrent());
        //while(LocalDateTime.now().getSecond()>=3);
        Task task1=new Task();
        task1.setBeanName("testService");
        task1.setMethodName("test");
        task1.setReturnTypeName("void");
        task1.setTag(null);
        task1.setTopic(null);
        task1.setParamList(new ArrayList<Param>());
        LocalDateTime now=LocalDateTime.now();

        task1.setBeginTime(now.plusSeconds(43));
        System.out.println("task1 beginTime:"+task1.getBeginTime()+" onWheel:"+(now.getSecond()+43));
        secondWheel.getCells().get((now.getSecond()+43)%60).add(task1);
        Task task2=new Task();
        task2.setBeanName("testService");
        task2.setMethodName("test");
        task2.setReturnTypeName("void");
        task2.setTag(null);
        task2.setTopic(null);
        task2.setParamList(new ArrayList<Param>());
        task2.setBeginTime(now.plusSeconds(44));
        System.out.println("task2 beginTime:"+task2.getBeginTime()+" onWheel:"+(now.getSecond()+44));
        secondWheel.getCells().get((now.getSecond()+44)%60).add(task2);
        Thread.sleep(36100);
        //这里后加prepareTime
        assertEquals(scheduleJob.getSecondWheel().getCurrent(),(LocalDateTime.now().getSecond()+10+1)%60);
    }
}