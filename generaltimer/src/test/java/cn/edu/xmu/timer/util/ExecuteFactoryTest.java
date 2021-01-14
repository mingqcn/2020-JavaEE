package cn.edu.xmu.timer.util;


import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.model.bo.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 执行工厂测试
 * @author ：Zeyao Feng
 * @date ：Created in 2020-12-02 13:38
 */
@SpringBootTest(classes = Application.class)
public class ExecuteFactoryTest {

    @Autowired
    ExecuteFactory executeFactory;
    /**
    * 测试Task的topic为null时是否返回一个LocalExecute对象
    * @Author: Zeyao Feng
    * @Date: Created in 2020-12-02 13:43
    */
    @Test
    public void testLocalExecute(){
        Task task=new Task();
        task.setTopic(null);
        Object obj=executeFactory.getExecuteWay(task);
        assertEquals(LocalExecute.class, obj.getClass());
    }


    /**
    * 测试Task的topic为非null时是否返回一个RemoteRocketMqExecute对象
    * @Author: Zeyao Feng
    * @Date: Created in 2020-12-02 13:44
    */
    @Test
    public void testRemoteRocketMqExecute(){
        Task task=new Task();
        task.setTopic("1");
        Object obj=executeFactory.getExecuteWay(task);
        assertEquals(RemoteRocketMQExecute.class, obj.getClass());
    }


}