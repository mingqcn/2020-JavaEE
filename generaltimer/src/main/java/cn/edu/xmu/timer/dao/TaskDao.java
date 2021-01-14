package cn.edu.xmu.timer.dao;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.mapper.ParamPoMapper;
import cn.edu.xmu.timer.mapper.TaskPoMapper;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.po.ParamPo;
import cn.edu.xmu.timer.model.po.ParamPoExample;
import cn.edu.xmu.timer.model.po.ParamPo;
import cn.edu.xmu.timer.model.po.ParamPoExample;
import cn.edu.xmu.timer.model.po.TaskPo;
import cn.edu.xmu.timer.model.po.TaskPoExample;
import cn.edu.xmu.timer.service.ScheduleJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.*;

/**
 * @Author Pinzhen Chen
 * @Date 2020/12/2 8:24
 */
@Repository
public class TaskDao {


    @Autowired
    private TaskPoMapper taskPoMapper;

    @Autowired
    private ParamPoMapper paramPoMapper;


    private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);

    /**
     * 通过bo对象获取po对象
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/2 8:42
     */
    private TaskPo getTaskPoByTask(Task bo){
        TaskPo po=new TaskPo();
        po.setBeginTime(bo.getBeginTime());
        po.setReturnTypeName(bo.getReturnTypeName());
        po.setMethodName(bo.getMethodName());
        po.setTag(bo.getTag());
        if(bo.getTopic()==null)
            po.setTopic("default");
        else
            po.setTopic(bo.getTopic());
        po.setPeriod(bo.getPeriod());
        po.setBeanName(bo.getBeanName());
        return po;
    }

    /**
     * 通过po创建bo对象
     * @author 24320182203173 Chen Pinzhen
     * @date: 2020/12/2 8:49
     */
    private Task getTaskByTaskPo(TaskPo po){
        Task bo=new Task();
        bo.setId(po.getId());
        bo.setBeginTime(po.getBeginTime());
        bo.setGmtCreate(po.getGmtCreate());
        bo.setGmtModified(po.getGmtModified());
        bo.setTopic(po.getTopic());
        bo.setTag(po.getTag());
        bo.setReturnTypeName(po.getReturnTypeName());
        bo.setMethodName(po.getMethodName());
        bo.setPeriod(po.getPeriod());
        bo.setBeanName(po.getBeanName());
        //计算sendTime
//        LocalDateTime sendTime=po.getBeginTime().minusSeconds(prepareTime);
//        bo.setSendTime(sendTime);
        if(po.getTopic().equals("default")){
            bo.setSenderName("localExecute");
            bo.setTopic(null);
        }
        else
            bo.setSenderName("remoteRocketMQExecute");
        return bo;
    }

    /**
         * 通过po创建bo对象
         * @param po
         * @return Param
         * @author 24320182203173 Chen Pinzhen
         * @date: 2020/12/3 8:39
         */
    private Param getParamByParamPo(ParamPo po){
        Param bo=new Param();
        bo.setId(po.getId());
        bo.setGmtCreate(po.getGmtCreate());
        bo.setGmtModified(po.getGmtModified());
        bo.setParamValue(po.getParamValue());
        bo.setSeq(po.getSeq());
        bo.setTypeName(po.getTypeName());
        return bo;
    }

    /**
         * 通过bo创建bo对象
         * @param bo
         * @return ParamPo
         * @author 24320182203173 Chen Pinzhen
         * @date: 2020/12/3 8:48
         */
    private ParamPo getParamPoByParam(Param bo){
        ParamPo po=new ParamPo();
        po.setParamValue(bo.getParamValue());
        po.setSeq(bo.getSeq());
        po.setTypeName(bo.getTypeName());
        return po;
    }

    /**
         * timer001: 创建定时任务
         * @param task
         * @param period
         * @return ReturnObject<Task>
         * @author 24320182203173 Chen Pinzhen
         * @date: 2020/12/2 8:39
         */
    @Transactional
    public ReturnObject<Task> createTask(Task task, Integer period) {
        task.setPeriod(period.byteValue());
        TaskPo taskPo = getTaskPoByTask(task);
        taskPo.setPeriod(period.byteValue());
        ReturnObject<Task> retObj = null;
        try{
            int ret = taskPoMapper.insertSelective(taskPo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertTask: insert task fail " + taskPo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + taskPo.getBeanName()));
            } else {
                //插入taskPo成功
                logger.debug("insertTask: insert task = " + taskPo.toString());
                task.setId(taskPo.getId());
                task.setGmtCreate(taskPo.getGmtCreate());
                List<Task> retList=new ArrayList<>();
                retList.add(task);
                //将新建任务放入时间轮
                //scheduleJob.addJob(retList);
                retObj = new ReturnObject<>(task);
                Task taskRet = getTaskByTaskPo(taskPo);
                taskRet.setGmtCreate(LocalDateTime.now());
                List<Param> paramList=task.getParamList();
                if(paramList!=null && !paramList.isEmpty()){
                    //把task中每个参数插入param表中
                    List<Param> retParamList = new ArrayList<>();
                    for (Param param : paramList) {
                        ParamPo paramPo=getParamPoByParam(param);
                        //为paramPo设置当前的taskId
                        paramPo.setTaskId(taskRet.getId());
                        paramPoMapper.insertSelective(paramPo);
                        //插入paramPo成功
                        Param retParam = getParamByParamPo(paramPo);
                        retParam.setGmtCreate(LocalDateTime.now());
                        retParamList.add(retParam);
                    }
                    //按照seq升序排序
                    retParamList.sort(Comparator.comparing(Param::getSeq));
                    taskRet.setParamList(new ArrayList<>(retParamList));
                }
//                List<Task> retList=new ArrayList<>();
//                retList.add(taskRet);
//                //将新建任务放入时间轮
//                scheduleJob.addJob(retList);
                retObj = new ReturnObject<>(taskRet);
            }
        }
        catch (DataAccessException e) {
            // 其他数据库错误
            logger.debug("other sql exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }


    /**
         * timer001: 获取topic下的所有定时任务
         * @param topic
         * @param tag
         * @return ReturnObject<List<Task>>
         * @author 24320182203173 Chen Pinzhen
         * @date: 2020/12/2 8:52
         */
    public ReturnObject<List<Task>> getTaskByTopic(String topic, String tag) {
        TaskPoExample taskPoExample=new TaskPoExample();
        TaskPoExample.Criteria criteria=taskPoExample.createCriteria();
        criteria.andTopicEqualTo(topic);
        if(tag!=null){
            criteria.andTagEqualTo(tag);
        }
        ReturnObject<List<Task>> retObj = null;
        try{
            List<TaskPo> taskPos = taskPoMapper.selectByExample(taskPoExample);
                logger.debug("selectTaskList: select Task = " + taskPos.toString());
                List<Task> taskList=new ArrayList<>();
            for (TaskPo po : taskPos) {
                Task task = getTaskByTaskPo(po);
                //根据taskid找到param
                ParamPoExample paramPoExample=new ParamPoExample();
                ParamPoExample.Criteria criteria1=paramPoExample.createCriteria();
                criteria1.andTaskIdEqualTo(po.getId());
                List<ParamPo> paramPos=paramPoMapper.selectByExample(paramPoExample);
                List<Param> paramList=new ArrayList<>();
                for (ParamPo paramPo : paramPos) {
                    Param param=getParamByParamPo(paramPo);
                    paramList.add(param);
                }
                //加入参数列表
                task.setParamList(new ArrayList<>(paramList));
                taskList.add(task);
            }
                retObj = new ReturnObject<>(taskList);
        }
        catch (DataAccessException e) {
                // 数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    @Transactional
    public ReturnObject removeTask(Long id){
        try {
            ParamPoExample example=new ParamPoExample();
            ParamPoExample.Criteria criteria=example.createCriteria();
            criteria.andTaskIdEqualTo(id);
            paramPoMapper.deleteByExample(example);
            taskPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject();
        }
        catch (DataAccessException e){
            logger.error("removeTask: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("otherError: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    @Transactional
    public ReturnObject cleanTaskByTopic(String topic, String tag){
        try {
            List<Long> list = new ArrayList<>();
            TaskPoExample example=new TaskPoExample();
            TaskPoExample.Criteria criteria=example.createCriteria();
            criteria.andTopicEqualTo(topic);
            if(!Objects.equals(null,tag))
            {
                criteria.andTagEqualTo(tag);
            }
            List<TaskPo> taskPos=taskPoMapper.selectByExample(example);
            for(TaskPo po:taskPos)
            {
                if(Objects.equals(po.getPeriod(),(byte) 8))
                {
                    list.add(po.getId());
                }
                else if(Objects.equals(po.getPeriod(),(byte) 0))
                {
                    LocalDate localDate=LocalDate.of(po.getBeginTime().getYear(),po.getBeginTime().getMonth(),po.getBeginTime().getDayOfMonth());
                    if(Objects.equals(localDate,LocalDate.now()) || Objects.equals(localDate,LocalDate.now().plusDays(1)))
                    {
                        list.add(po.getId());
                    }
                }
                else if( Objects.equals(po.getPeriod(),(byte)10))
                {
                    LocalDateTime localDateTime=po.getBeginTime();
                    if(Objects.equals(localDateTime.getMonth(),LocalDateTime.now().getMonth()) && Objects.equals(localDateTime.getDayOfMonth(),LocalDateTime.now().getDayOfMonth()))
                    {
                        list.add(po.getId());
                    }
                    if(Objects.equals(localDateTime.getMonth(),LocalDateTime.now().plusDays(1).getMonth()) && Objects.equals(localDateTime.getDayOfMonth(),LocalDateTime.now().plusDays(1).getDayOfMonth()))
                    {
                        list.add(po.getId());
                    }
                }
                else if(Objects.equals(po.getPeriod(),(byte)9))
                {
                    int day=po.getBeginTime().getDayOfMonth();
                    int day1=LocalDateTime.now().getDayOfMonth();
                    int day2=LocalDateTime.now().plusDays(1).getDayOfMonth();
                    if(day==day1 || day==day2)
                    {
                        list.add(po.getId());
                    }
                }
                else {
                    DayOfWeek day = po.getBeginTime().getDayOfWeek();
                    DayOfWeek day1 = LocalDateTime.now().getDayOfWeek();
                    DayOfWeek day2 = LocalDateTime.now().plusDays(1).getDayOfWeek();
                    if(day==day1 || day==day2)
                    {
                        list.add(po.getId());
                    }
                }
                ParamPoExample example1=new ParamPoExample();
                ParamPoExample.Criteria criteria1=example1.createCriteria();
                criteria1.andTaskIdEqualTo(po.getId());
                paramPoMapper.deleteByExample(example1);
                taskPoMapper.deleteByPrimaryKey(po.getId());
            }
            return new ReturnObject<>(list);
        }
        catch (DataAccessException e){
            logger.error("cleanTaskByTopic: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("otherError: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }


    /**
     * @author cxr
     * 把半天的任务找出来
     * @param beginTime 起始时间
     * @param endTime 终止时间
     * @return 12个小时的定时、定期任务
     */
    public ReturnObject loadNextDayTask(LocalDateTime beginTime, LocalDateTime endTime){
        //从数据库捞数据
        TaskPoExample taskPoExample = new TaskPoExample();
        TaskPoExample.Criteria criteria = taskPoExample.createCriteria();
        TaskPoExample.Criteria criteria1 = taskPoExample.createCriteria();
        TaskPoExample.Criteria criteria2 = taskPoExample.createCriteria();
        TaskPoExample.Criteria criteria3 = taskPoExample.createCriteria();

        //定时任务
        criteria.andBeginTimeBetween(beginTime,endTime);
        criteria.andPeriodEqualTo((byte)0);
        //定期任务（小时）
        criteria1.andPeriodEqualTo((byte)8);

        //定期任务(一周）
        Byte dayOfWeek = (byte)(beginTime.getDayOfWeek().getValue());
        criteria2.andPeriodEqualTo(dayOfWeek);

        //定期任务(月或年)
        criteria3.andPeriodGreaterThanOrEqualTo((byte)9);

        taskPoExample.or(criteria);
        taskPoExample.or(criteria1);
        taskPoExample.or(criteria2);
        taskPoExample.or(criteria3);

        List<Task> taskList;
        try{
            List<TaskPo> taskPoList = taskPoMapper.selectByExample(taskPoExample);
            taskList = new ArrayList<>(taskPoList.size());
            for(TaskPo po : taskPoList){
                if(po.getPeriod().equals(dayOfWeek) || po.getPeriod()==9 || po.getPeriod() == 10){
                    LocalDateTime date = updateDate(po.getPeriod(),beginTime,po.getBeginTime());
                    if(date == null || date.isBefore(beginTime)||date.isAfter(endTime)){
                        continue;
                    }
                    po.setBeginTime(date);
                }
                Task task = new Task();
                task.setId(po.getId());
                task.setBeginTime(po.getBeginTime());
                task.setSenderName(po.getSenderName());
                task.setBeanName(po.getBeanName());
                task.setMethodName(po.getMethodName());
                task.setReturnTypeName(po.getReturnTypeName());
                task.setTopic(po.getTopic());
                task.setTag(po.getTag());
                task.setGmtCreate(po.getGmtCreate());
                task.setGmtModified(po.getGmtModified());
                task.setPeriod(po.getPeriod());
                //根据id获取参数
                ParamPoExample paramPoExample = new ParamPoExample();
                ParamPoExample.Criteria paramCriteria = paramPoExample.createCriteria();
                paramCriteria.andTaskIdEqualTo(task.getId());
                List<ParamPo> paramPoList = paramPoMapper.selectByExample(paramPoExample);
                List<Param> paramList = new ArrayList<>(paramPoList.size());
                for(ParamPo paramPo : paramPoList){
                    Param param = new Param();
                    param.setId(paramPo.getId());
                    param.setSeq(paramPo.getSeq());
                    param.setTypeName(paramPo.getTypeName());
                    param.setParamValue(paramPo.getParamValue());
                    param.setGmtCreate(paramPo.getGmtCreate());
                    param.setGmtModified(paramPo.getGmtModified());
                    paramList.add(param);
                }
                //对参数进行排序
                Collections.sort(paramList, new Comparator<Param>() {
                    @Override
                    public int compare(Param o1, Param o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });
                task.setParamList(paramList);
                if(po.getPeriod() == 8){
                    for(int i=0;i<12;i++){
                        Task periodTask = new Task();
                        periodTask.setId(task.getId());
                        periodTask.setBeanName(task.getBeanName());
                        periodTask.setSenderName(task.getSenderName());
                        periodTask.setMethodName(task.getMethodName());
                        periodTask.setReturnTypeName(task.getReturnTypeName());
                        periodTask.setTopic(task.getTopic());
                        periodTask.setTag(task.getTag());
                        periodTask.setGmtCreate(task.getGmtCreate());
                        periodTask.setGmtModified(task.getGmtModified());
                        periodTask.setParamList(task.getParamList());
                        periodTask.setPeriod(task.getPeriod());
                        //处理时间
                        LocalDateTime taskTime = beginTime.plusHours(i).plusMinutes(task.getBeginTime().getMinute())
                                .plusSeconds(task.getBeginTime().getSecond());
                        periodTask.setBeginTime(taskTime);
                        taskList.add(periodTask);
                    }
                }
                else{
                    taskList.add(task);
                }
            }
            return new ReturnObject(taskList);
        } catch (DataAccessException e){
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        } catch (Exception e){
            logger.error("其他错误：" + e.getMessage());
            return new ReturnObject(ResponseCode.INTERNAL_SERVER_ERR, String.format("其他错误：%s", e.getMessage()));
        }
    };

    private LocalDateTime updateDate(Byte type, LocalDateTime time, LocalDateTime poTime){
        boolean legal = true;
        switch(type){
            case 10:
                legal = poTime.getMonth().equals(time.getMonth());
            case 9:
                if(legal) {
                    legal = (poTime.getDayOfMonth() == time.getDayOfMonth());
                }
        }
        LocalDateTime date = LocalDateTime.of(time.getYear(),time.getMonth(), time.getDayOfMonth(),
                poTime.getHour(), poTime.getMinute(), poTime.getSecond());
        if(legal){
            return date;
        }else{
            return null;
        }
    }

    public void setTaskPoMapper(TaskPoMapper poMapper){
        this.taskPoMapper = poMapper;
    }

    public void setParamPoMapper(ParamPoMapper poMapper){
        this.paramPoMapper = poMapper;
    }

}
