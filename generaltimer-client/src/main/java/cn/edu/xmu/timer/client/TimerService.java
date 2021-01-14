package cn.edu.xmu.timer.client;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.model.bo.Task;

import java.util.List;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 17:42
 **/
public interface TimerService {

    /**
     * 创建定时任务
     * @param task 任务对象
     * @param period 周期
     * @return
     */
    ReturnObject<Task> createTask(Task task, Integer period);

    /**
     * 清除定时任务
     * @param id task id
     * @return
     */
    ReturnObject removeTask(Long id);

    /**
     * 获取topic下的所有定时任务
     * @param topic topic名称
     * @param tag tag名称（null返回topic下所有定时任务）
     * @return
     */
    ReturnObject<List<Task>> getTaskByTopic(String topic, String tag);

    /**
     * 清除topic下的所有定时任务
     * @param topic topic名称
     * @param tag tag名称（null清除topic下所有定时任务）
     * @return
     */
    ReturnObject cleanTaskByTopic(String topic, String tag);
}
