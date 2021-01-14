package cn.edu.xmu.timer.util;

import cn.edu.xmu.timer.model.bo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;

/**
 * 根据Task类型选择执行类的工厂,将来迁移到测试
 * @author ：Zeyao Feng
 * @date ：Created in 2020-12-02 12:01
 * Modified in 2020-12-03 16:41
 */
@Component
public class ExecuteFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public TaskExecute getExecuteWay(Task task){
        if(task.getTopic()==null){
            return applicationContext.getBean("localExecute",TaskExecute.class);

        }
        else{
            return applicationContext.getBean("remoteRocketMQExecute",TaskExecute.class);

        }
    }
}