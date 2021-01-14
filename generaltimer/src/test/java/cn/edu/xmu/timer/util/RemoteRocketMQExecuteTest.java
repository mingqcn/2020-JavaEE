package cn.edu.xmu.timer.util;

import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.bo.TaskMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;

/**
 * 测试RocketMQ的TaskMessage消息发送
 * @author ：Zeyao Feng
 * @date ：Created in 2020-12-02 19:46
 */

@SpringBootTest(classes = Application.class)
public class RemoteRocketMQExecuteTest {
    @Autowired
    ExecuteFactory executeFactory;

    /**
    * 发送消息，输出该listener的消息进行测试
    * @Author: Zeyao Feng
    * @Date: Created in 2020-12-02 19:47
    */
    @Test
    public void rocketMQTest() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Task task=new Task();
        task.setTopic("1");
        task.setBeanName("test");
        task.setMethodName("test");
        executeFactory.getExecuteWay(task).execute(task);
    }
}