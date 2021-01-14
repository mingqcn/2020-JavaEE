package cn.edu.xmu.timer.util;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.bo.TaskMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;

/**
 * 远程使用rocketMQ执行
 * @author ：Zeyao Feng
 * @date ：Created in 2020-12-02 11:57
 * Modified in 2020-12-03 10:30
 */
@Component("remoteRocketMQExecute")
public class RemoteRocketMQExecute implements  TaskExecute{
    private static  final Logger logger = LoggerFactory.getLogger(RemoteRocketMQExecute.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void execute(Task task) {
        TaskMessage message=task.createTaskMessage();
        String json= JacksonUtil.toJson(message);
        Message m = MessageBuilder.withPayload(json).build();
        rocketMQTemplate.sendOneWay(task.getTopic(),m);
        logger.debug("RemoteRocketMQExecute Task id:"+task.getId());
    }

    @Override
    public void internalExecute(Task task) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        logger.debug("RemoteRocketMQExecute skip the external Task id:"+task.getId());
    }
}