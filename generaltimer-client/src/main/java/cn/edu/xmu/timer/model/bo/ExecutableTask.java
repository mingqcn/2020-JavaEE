package cn.edu.xmu.timer.model.bo;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author xincong yao
 * @date 2020-11-19
 * Modified By Ming Qiu 2020-12-01
 */
@Data
public class ExecutableTask {

    private Object service;

    private Method serviceMethod;

    private List<Object> paramValues;

    private LocalDateTime beginTime;

    public Object execute() throws InvocationTargetException, IllegalAccessException {
        return serviceMethod.invoke(service, paramValues.toArray());
    }
}
