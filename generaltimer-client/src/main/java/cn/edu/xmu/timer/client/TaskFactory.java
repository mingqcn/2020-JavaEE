package cn.edu.xmu.timer.client;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.timer.model.bo.ExecutableTask;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.TaskMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * @author xincong yao
 * @date 2020-11-22
 * Modified By Ming Qiu
 */
@Service
public class TaskFactory {

    @Autowired
    private ApplicationContext applicationContext;

    public ExecutableTask getExecutableTask(TaskMessage message) throws ClassNotFoundException, NoSuchMethodException {
        ExecutableTask et = new ExecutableTask();

        et.setBeginTime(message.getBeginTime());

        int paramSize = message.getParamList().size();
        List<Class> paramTypes = new ArrayList<>(paramSize);
        List<Object> paramValues = new ArrayList<>(paramSize);
        for (Param p : message.getParamList()) {
            Class c = Class.forName(p.getTypeName());
            paramTypes.add(c);
            paramValues.add(JacksonUtil.toObj(p.getParamValue(), c));
        }

        et.setParamValues(paramValues);

        Object service = applicationContext.getBean(message.getBeanName());
        et.setService(service);
        Method method = service.getClass().getMethod(message.getMethodName(), paramTypes.toArray(new Class[0]));
        et.setServiceMethod(method);
        return et;
    }
}
