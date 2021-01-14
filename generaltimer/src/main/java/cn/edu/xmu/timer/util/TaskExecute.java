package cn.edu.xmu.timer.util;

import cn.edu.xmu.timer.model.bo.Task;

import java.lang.reflect.InvocationTargetException;

/**
* 任务执行接口
* @Author: Zeyao Feng
* @Date: Created in 2020-12-02 12:03
* Modified in 2020-12-03 10:30
*/
public interface TaskExecute {
    public void execute(Task task) throws NoSuchMethodException, ClassNotFoundException, InvocationTargetException, IllegalAccessException;
    public void internalExecute(Task task) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
