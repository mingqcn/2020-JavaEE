package cn.edu.xmu.timer.service.impl;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.client.TimerService;
import cn.edu.xmu.timer.dao.TaskDao;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.service.ScheduleJob;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 20:45
 **/
@Service
@DubboService
public class TimeServiceImpl implements TimerService {

    @Autowired
    private ScheduleJob scheduleJob;

    @Autowired
    private TaskDao taskDao;

    /**
     * timer001: 创建定时任务
     * @param task 任务对象
     * @param period 周期
     * @return
     */
    @Override
    public ReturnObject<Task> createTask(Task task, Integer period) {
        ReturnObject<Task> returnObject=taskDao.createTask(task,period);
        if(returnObject.getCode()==ResponseCode.OK){
            List<Task> taskList=new ArrayList<>();
            taskList.add(returnObject.getData());
            scheduleJob.addJob(taskList);
        }
        return returnObject;
    }

    /**
     * timer002: 清除定时任务
     * @param id task id
     * authored by Li Di han
     * @return
     */
    @Override
    public ReturnObject removeTask(Long id) {
        ReturnObject returnObject= taskDao.removeTask(id);
        if(returnObject.getCode()== ResponseCode.OK) {
            List<Long> list = new ArrayList<>();
            list.add(id);
            scheduleJob.removeJob(list);
        }
        return returnObject;
    }

    /**
     * timer001：获取topic下的所有定时任务
     * @param topic topic名称
     * @param tag tag名称（null返回topic下所有定时任务）
     * @return
     */
    @Override
    public ReturnObject<List<Task>> getTaskByTopic(String topic, String tag) {
        return taskDao.getTaskByTopic(topic,tag);
    }

    /**
     * timer002: 清除topic下的所有定时任务
     * @param topic topic名称
     * @param tag tag名称（null清除topic下所有定时任务）
     * authored by Li Di han
     * @return
     */
    @Override
    public ReturnObject cleanTaskByTopic(String topic, String tag) {
        ReturnObject<List<Long>> returnObject= taskDao.cleanTaskByTopic(topic,tag);
        if(!Objects.equals(returnObject.getData(),null))
        {
            List<Long> list=returnObject.getData();
            scheduleJob.removeJob(list);
        }
        return returnObject;
    }
}
