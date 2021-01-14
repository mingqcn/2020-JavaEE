package cn.edu.xmu.timer.util;

import cn.edu.xmu.timer.client.TaskFactory;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.bo.TaskMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

/**
 * 本地执行
 * @author ：Zeyao Feng
 * @date ：Created in 2020-12-02 11:55
 * Modified in 2020-12-03 10:30
 */
@Component("localExecute")
public class LocalExecute implements TaskExecute{
    private static  final Logger logger = LoggerFactory.getLogger(LocalExecute.class);

    @Autowired
    private TaskFactory taskFactory;

    @Override
    public void execute(Task task) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        TaskMessage message= task.createTaskMessage();
        taskFactory.getExecutableTask(message).execute();
        logger.debug("LocalExecute Task id:"+task.getId());
    }

    @Override
    public void internalExecute(Task task) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        execute(task);
    }
}