package cn.edu.xmu.timer.service;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.model.bo.*;
import cn.edu.xmu.timer.client.TaskFactory;
import cn.edu.xmu.timer.model.bo.TaskMessage;
import cn.edu.xmu.timer.service.impl.TimeServiceImpl;
import cn.edu.xmu.timer.util.ExecuteFactory;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.xmu.timer.dao.TaskDao;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.model.bo.TimeWheel;
import org.springframework.beans.factory.InitializingBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;



import java.lang.reflect.InvocationTargetException;

import java.time.Duration;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.*;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 20:57
 **/
@Service
public class ScheduleJob implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(ScheduleJob.class);

    @Autowired
    private TaskFactory taskFactory;

    @Autowired
    private ExecuteFactory executeFactory;

    @Value("${generaltimer.prepare-time}")
    private Integer prepareTime;

    @Autowired
    private TaskDao taskDao;


    /**
     * 小时轮，每个格子30分钟
     */
    private TimeWheel hourWheel;

    /**
     * 分钟轮，每个格子30秒
     */
    private TimeWheel minuteWheel;

    /**
     * 秒轮，每个格子1秒
     */
    private TimeWheel secondWheel;


    /**
     * @author Liangji3229
     * 测试用，设置秒轮
     * @param secondWheel
     */
    public void setSecondWheel(TimeWheel secondWheel){
        this.secondWheel=secondWheel;
    }
    /**
     * timer009: 启动初始化
     * 按照启动时间，把12小时的内容装载入时间轮
     * @return
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        this.hourWheel = new TimeWheel(48, 0);

        this.minuteWheel = new TimeWheel(120, 0);

        this.secondWheel = new TimeWheel(60, 0);

        System.out.println("afterPropertiesSet: "+minuteWheel.toString());
    }

    /**
     * timer003: 将下一半天的任务装载到小时轮上(注意提前量)
     *  1. 小时周期任务需要在每个小时重复放
     *  2. 要把下一次的半天装载任务定义在小时格中，放在第0，1，2，3，4，5个小时中任务最少的完成
     * @author cxr
     * @param beginTime 起
     * @param endTime 止
     * @return
     */
    @Async("jobExecutor")
    public void loadNextDayTask(LocalDateTime beginTime, LocalDateTime endTime){
        ReturnObject retObject = taskDao.loadNextDayTask(beginTime, endTime);
        if(retObject.getCode() != ResponseCode.OK){
            logger.error("数据库错误: " + retObject.getErrmsg());
            return;
        }

        //获取要装载的任务
        List<Task> loadTaskList = (List<Task>)retObject.getData();

        List<Task>[] lists = getAllocatedLists(hourWheel, loadTaskList);
        //找到任务数最小的格子
        int minIndex = getMinIndex(hourWheel, lists);
        createTask(hourWheel, minIndex, lists, beginTime, endTime);

/*        final int hourWheelListNum = 24; //小时轮的一半轮子数量
//        List<Task>[] lists = new List[hourWheelListNum];
//
//        //初始化时轮上的每个格子（时间片）
//        for(int i = 0; i < hourWheelListNum; i++){
//            lists[i] = new ArrayList<Task>();
//        }
//
//        //记录每个时间的任务数,用于找任务数最小的格子
//        int[] taskNum = new int[hourWheelListNum / 2];
//
//        for (Task task : loadTaskList) {
//            int time = (task.getBeginTime().getHour()*2+task.getBeginTime().getMinute()/30) % 24;
//            lists[time].add(task);
//            if (time < hourWheelListNum / 2) {
//                taskNum[time]++;
//            }
//        }
//
//        //记录任务数最少量
//        int minNum = taskNum[0];
//        //记录任务数最少的时刻
//        int minRecord = 0;
//
//        for(int i = 1; i < hourWheelListNum / 2; i++){
//            if(taskNum[i] < minNum) {
//                minNum = taskNum[i];
//                minRecord = i;
//            }
//        }
//
//        Map<Integer,List<Task>> map = new TreeMap<>();
//        for(int i = 0 ; i < hourWheelListNum; i++){
//            map.put(i,lists[i]);
//        }
//
//        Task task = new Task();
//        task.setBeanName(ScheduleJob.class.getName());
//        task.setSenderName(ScheduleJob.class.getName());
//        task.setMethodName("loadNextDayTask");
//        task.setReturnTypeName("void");
//        task.setTag(null);
//        task.setTopic(null);
//        //添加函数参数
//        LocalDateTime beginTimeParam = endTime, endTimeParam = endTime.plusHours(12);
//        List<Param> taskParam = new ArrayList<>(2);
//        Param param1 = new Param();
//        param1.setSeq(0);
//        param1.setTypeName("LocalDateTime");
//        param1.setParamValue(beginTimeParam.toString());
//        Param param2 = new Param();
//        param2.setSeq(1);
//        param2.setTypeName("LocalDateTime");
//        param2.setParamValue(endTimeParam.toString());
//        taskParam.add(param1);
//        taskParam.add(param2);
//        task.setParamList(taskParam);
//        //计算运行时间，运行时间处于最少任务量的格子的中间时间，因而加了15分钟
//        task.setBeginTime(beginTime.plusMinutes(minRecord*30 + 15));
//        //===========================================
//        lists[minRecord].add(task);
//        //加入下一次装载任务
//        map.put(minRecord,lists[minRecord]);
//
//        log.debug("loadNextDayTask thread: "+Thread.currentThread().toString());
//
//        log.debug("loadNextDayTask map: " + map.toString());
//
//        hourWheel.loadNextRound(map);
//
//        log.debug("loadNextDayTask cells: " + this.getHourWheel().getCells().toString());
 */
    }

    /**
     * timer004: 将下一半小时的任务装载到分钟轮上
     * 1. 要把下一次的半小时装载任务定义在分钟格中，放在第0 - 14分钟中任务最少的完成
     * @return
     */
    @Async("jobExecutor")
    public void loadNextHourTask() {
        List<Task> loadTaskList = loadNextCell(minuteWheel);
        List<Task>[] lists = getAllocatedLists(minuteWheel, loadTaskList);
        //找到任务数最小的格子
        int minIndex = getMinIndex(minuteWheel, lists);
        createTask(minuteWheel, minIndex, lists, null, null);
    }


    /**
     * timer005: 将下一半分钟的任务装载到秒轮上
     * 1. 要把下一次的半分钟装载任务定义在秒格中，放在第0 - 14秒中任务最少的完成
     *
     * @author 3218
     * @return
     */
    @Async("jobExecutor")
    public void loadNextMinuteTask(){
        //获取要装载的任务
        List<Task> loadTaskList = minuteWheel.nextCell();


        List<Task>[] lists = new List[30];

        //初始化
        for(int i = 0; i < 30; i++){
            lists[i] = new ArrayList<Task>();
        }

        //记录每个时间的任务数
        int[] taskNum = new int[15];

        for (Task task : loadTaskList) {
            int time = task.getBeginTime().getSecond() % 30;
            lists[time].add(task);
            if (time < 15) {
                taskNum[time]++;
            }
        }


        //记录任务数最少量
        int minNum = taskNum[0];
        //记录任务数最少的时刻
        int minRecord = 0;

        for(int i = 1; i < 15; i++){
            if(taskNum[i] < minNum) {
                minNum = taskNum[i];
                minRecord = i;
            }
        }

        createTask(secondWheel,minRecord,lists,null,null);
    }

    /**
     * timer006: 每秒钟执行一次, 执行秒轮的任务
     * @author LiangJi3229
     * 1. 判断任务是本地任务还是远程任务， 本地任务topic=null，远程任务向消息队列发送消息
     */
    final Boolean lockPreRunTaskWrite=true,preRunTaskTimeReadWrite=true,lockPreRunTaskRead=true;
    Long fullDay=24*60*60L;
    private void updatePreRunTaskTime(LocalTime taskTime){
        if (preRunTaskTime == null) {
            preRunTaskTime = taskTime;
        } else {
            preRunTaskTime = preRunTaskTime.isAfter(taskTime) ? preRunTaskTime : taskTime;
        }
    }
    //由于采用LocalTime 取得间隔时间需要获得一个小于半天的值
    private Long getRealGapTime(Long gapTime){
        if (gapTime>=fullDay/2) {
            gapTime-=fullDay;
        }
        if (gapTime<=-fullDay/2) {
            gapTime+=fullDay;
        }
        return gapTime;
    }
    private void executeTaskByTime(List<Task> tasks,LocalTime now, Long gapTime) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (tasks==null) {
            return;
        }

        //lastRunTaskTime=tasks.get(0).getBeginTime();
        LocalTime taskTime=tasks.get(0).getBeginTime().toLocalTime();
        //System.out.println("execute Task Time: "+taskTime.toString()+"real Time:"+now.toString());
        //当前任务的时间早于现在的真实时间，如果提前太多，将本地任务存储以免覆盖，并将其他任务执行
        if(gapTime<0) {
            if (now.getSecond() / 30 == (now.getSecond() + gapTime) / 30) {
                synchronized (preRunTaskTimeReadWrite) {
                    updatePreRunTaskTime(taskTime);
                }
                for (Task task : tasks) {
                    executeFactory.getExecuteWay(task).execute(task);
                }
            } else {
                for (Task task : tasks) {
                    if (task.getTopic() == null) {
                        preRunTask=task;
                    }
                    else{
                        executeFactory.getExecuteWay(task).execute(task);
                    }
                }
            }
        }
        else{
            if(gapTime<=prepareTime/2){
                for(Task task:tasks){
                    executeFactory.getExecuteWay(task).execute(task);
                }
            }
            else{
                //运行本地任务
            }
        }


    }
    //int gapTimeLimit=25;//最大差值时间为25s
    LocalTime preRunTaskTime;//超前运行的任务的时间
    Task preRunTask;//第一次读到的不在同一半轮的本地任务
    int adjustPrepareTime(int prepareTime){
        return prepareTime;
    }
    @Scheduled(cron = "*/1 * * * * ?")
    @Async("jobExecutor")
    public void execute() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        LocalTime now = LocalTime.now();
        //if(preRunTask!=null)System.out.println("preRunTaskSave:"+preRunTask.getBeginTime().toString());

        //在此处设置准备时间
        //prepareTime=adjustPrepareTime(prepareTime);
        //假定当前时间为prepareTime后的时间，执行now+prepareTime之前的任务
        now = now.plusSeconds(prepareTime);
        logger.debug("now+prepareTime: "+now+" current: "+secondWheel.getCurrent());

        synchronized (lockPreRunTaskRead){
            if (preRunTask != null) {
                logger.debug("now:"+now.toString()+"preRunTask:"+preRunTask.getBeginTime().toLocalTime().toString());
                if (preRunTask.getBeginTime().toLocalTime().withNano(0).equals(now.withNano(0))) {
                    executeFactory.getExecuteWay(preRunTask).execute(preRunTask);
                    synchronized (preRunTaskTimeReadWrite) {
                        updatePreRunTaskTime(preRunTask.getBeginTime().toLocalTime());
                    }
                    preRunTask = null;
                }
                return;
            }
        }
        //这里需要运算以作出判断（防止跨天）
        synchronized (preRunTaskTimeReadWrite){
            if(preRunTaskTime!=null) {
                Long gapTime = Duration.between(preRunTaskTime, now).toSeconds();
                gapTime = getRealGapTime(gapTime);
                logger.debug("gapTime:"+gapTime);
                if (gapTime < 0) {
                    return;
                }
            }
        }
        //if(!preRunTaskTime.isBefore(now)){return;}
        List<Task> tasks=secondWheel.nextCell();
        logger.debug("current:"+getSecondWheel().getCurrent()+"task size:"+tasks.size());
        if(tasks.size()==0){return;}
        Long gapTime=Duration.between(tasks.get(0).getBeginTime().toLocalTime(),now).toSeconds();
        gapTime=getRealGapTime(gapTime);
        //System.out.println("gapTime:"+gapTime);
        executeTaskByTime(tasks,now,gapTime);
        while(gapTime>=0&&(tasks.size()==0||tasks.get(0).getBeginTime().toLocalTime().isBefore(now))){
            gapTime--;
            logger.debug("gapTime:"+gapTime);
            tasks=secondWheel.nextCell();

            if(tasks.size()!=0){
                executeTaskByTime(tasks,now,gapTime);
                gapTime=Duration.between(tasks.get(0).getBeginTime().toLocalTime(),now).toSeconds();
                gapTime=getRealGapTime(gapTime);
            }
        }

    }

    /**
     * timer007: 在时间轮里清除任务
     *
     * @param taskIds 任务的id的列表
     * @return
     */
    public void removeJob(List<Long> taskIds){
        // 若传入 id 为空则认为不需要进行清除
        if (null == taskIds) {
            return;
        }
        if (null != hourWheel) {
            this.hourWheel.removeTask(taskIds);
        }
        if (null != this.minuteWheel) {
            this.minuteWheel.removeTask(taskIds);
        }
        if (null != this.secondWheel) {
            this.secondWheel.removeTask(taskIds);
        }
    }

    /**
     * timer008: 在时间轮里增加任务
     * @param tasklist 任务的id的列表
     * @return
     * 24320182203227 Li Zihan
     */
    public void addJob(List<Task> tasklist){

        for (Task task : tasklist) {
            Byte period=task.getPeriod();
            LocalDate localDate = LocalDate.now();//获得当前日期
            LocalTime localTime = LocalTime.now();//获得当前时间
            int localDay=localDate.getDayOfMonth();
            int localMonth=localDate.getMonthValue();
            LocalDateTime sendTime=task.getBeginTime();//获得任务时间
            LocalTime LocalSendTime=sendTime.toLocalTime();//获得任务开始时间
            int sendDay=sendTime.getDayOfMonth();
            int sendMonth=sendTime.getMonthValue();
            int seconds = LocalSendTime.getSecond()+LocalSendTime.getMinute()*60+LocalSendTime.getHour()*60*60;//获得当前秒数
            if(localDate==sendTime.toLocalDate()||period<=7||period>=1||(period==9&&localDay==sendDay)||(period==10&&localMonth==sendMonth&&localDay==sendDay)) {
                if (period ==0||(period>=8&&period<=10)||(period ==localDate.getDayOfWeek().getValue())) {
                    int index1=(int)seconds/1800;//获取小时轮的位置
                    int current1=hourWheel.getCurrent();//获取下一次小时轮的位置
                    if (index1>=current1) {//如果小时轮的位置在下一个及之后且不是每小时定期任务
                        hourWheel.getCells().get(index1).add(task);//直接插入后面的小时轮
                    }
                    else if((index1+1)%48==current1){//如果在当前小时轮
                        int seconds2=seconds-1800*index1;//获得距离当前小时轮的秒数
                        int index2=seconds2/30;//获得要插入的分钟轮位置
                        int current2=minuteWheel.getCurrent();//得到下一次分钟轮的位置
                        if(index2>=current2){//如果在下一次及之后的位置
                            minuteWheel.getCells().get(index2).add(task);//插入后面的分钟轮
                        }
                        else if((index2+1)%60==current2){//如果在当前分钟轮
                            int seconds3=seconds2-index2*30;//得到距离当前秒钟轮的秒数即要插入的秒钟轮位置
                            int current3=secondWheel.getCurrent();//得到当前秒钟轮位置
                            if (seconds3>=current3) {
                                //如果大于当前秒钟轮
                                secondWheel.getCells().get(seconds3).add(task);//插入任务
                            }
                        }
                    }
                }
                else if(period==8){
                    int current=hourWheel.getCurrent();//得到下一次小时轮的位置
                    int senconds_temp=seconds%3600;
                    int odd=current%2;
                    if (senconds_temp>=1800) {
                        for (int i = 1; i < 48; i = i + 2) {
                            hourWheel.getCells().get(i).add(task);//直接插入
                        }
                    }
                    else {
                        if (senconds_temp < 1800) {
                            for (int i = 0; i < 48; i = i + 2) {
                                hourWheel.getCells().get(i).add(task);//直接插入
                            }
                        }
                    }
                }
            }
        }
    }

    public TimeWheel getHourWheel() {
        return hourWheel;
    }

    public TimeWheel getMinuteWheel() {
        return minuteWheel;
    }

    public TimeWheel getSecondWheel() {
        return secondWheel;
    }

    /**
     * 加载下一块
     * @date  Created in 2020/12/7 上午12:01
     */
    private List<Task> loadNextCell(TimeWheel timeWheel){
        List<Task> loadTaskList = new ArrayList<Task>();
        loadTaskList = timeWheel.nextCell();
        return loadTaskList;
    }

    /**
     * 按照开始时间放置半个时间轮任务并返回
     * @date  Created in 2020/12/8 下午9:16
    */
    private List<Task>[] getAllocatedLists(TimeWheel timeWheel, List<Task> loadTaskList){
        //时间轮的一半格子数量
        final int wheelListNum = getWheelListNum(timeWheel);
        List<Task>[] lists = new List[wheelListNum];
        //初始化时轮上的每个格子（时间片）
        for(int i = 0; i < wheelListNum; i++){
            lists[i] = new ArrayList<Task>();
        }
        //将每个任务加入lists的对应时间片
        for (Task task : loadTaskList) {
            int time = calculateTimeGrid(timeWheel,task);
            lists[time].add(task);
        }
        return lists;
    }

    /**
     * 计算任务最少的格子序列
     * @date  Created in 2020/12/8 上午1:06
     */
    private Integer getMinIndex(TimeWheel timeWheel, List<Task>[] lists){
        //半个时间轮
        final int wheelListNum = getWheelListNum(timeWheel);
        //记录任务数最少的时刻
        int minRecord = Integer.MAX_VALUE;
        int minIndex = -1;

        //每次都找前1/4时间轮上任务最少的时间片
        for(int i = 0; i < wheelListNum/2; i++){
            if(lists[i].size() < minRecord) {
                minRecord = lists[i].size();
                minIndex = i;
            }
        }
        return minIndex;
    }

    /**
     * 在任务最少处创建该函数的任务 如果不为时轮则传入beginTime和endTime均为null
     * @date  Created in 2020/12/8 上午1:49
     */
    private void createTask(TimeWheel timeWheel, int minIndex, List<Task>[] lists, LocalDateTime beginTime, LocalDateTime endTime){
        Map<Integer,List<Task>> map = new TreeMap<>();
        for(int i = 0 ; i < getWheelListNum(timeWheel)/2; i++){
            map.put(i,lists[i]);
        }
        //Task任务的设置
        Task task = new Task();
        task.setBeanName(ScheduleJob.class.getName());
        task.setSenderName(ScheduleJob.class.getName());
        task.setMethodName(getTaskName(timeWheel));
        task.setReturnTypeName("void");
        task.setTag(null);
        task.setTopic(null);
        task.setParamList(calculateParamList(timeWheel, beginTime, endTime));
        //如果beginTime为null 说明不为时轮 则通过第0个任务的beginTime计算时间 同时判断当前任务是否为空 为空则使用现在时间作为加载时间
        LocalDateTime loadTime = beginTime;
        if (loadTime == null ){
            loadTime = (loadNextCell(timeWheel).isEmpty() ? LocalDateTime.now() : loadNextCell(timeWheel).get(0).getBeginTime());
        }
        task.setBeginTime(calculateSendTime(loadTime, minIndex, timeWheel));

        lists[minIndex].add(task);
        //加入下一次装载任务
        map.put(minIndex,lists[minIndex]);
        logger.debug(getTaskName(timeWheel)+" thread: "+Thread.currentThread().toString());
        logger.debug(getTaskName(timeWheel)+" map: "+map.toString());
        timeWheel.loadNextRound(map);
        logger.debug(getTaskName(timeWheel)+ timeWheel.getCells().toString());
    }

    /**
     * 计算三种时间轮 paramList
     * @date  Created in 2020/12/8 下午8:14
     */
    private List<Param> calculateParamList(TimeWheel timeWheel, LocalDateTime beginTime, LocalDateTime endTime){
        if(timeWheel.getNum() == 48){   //小时轮
            LocalDateTime beginTimeParam = endTime, endTimeParam = endTime.plusHours(12);
            List<Param> taskParam = new ArrayList<>(2);
            Param param1 = new Param();
            param1.setSeq(0);
            param1.setTypeName("LocalDateTime");
            param1.setParamValue(beginTimeParam.toString());
            Param param2 = new Param();
            param2.setSeq(1);
            param2.setTypeName("LocalDateTime");
            param2.setParamValue(endTimeParam.toString());
            taskParam.add(param1);
            taskParam.add(param2);
            return taskParam;
        }else{
            return new ArrayList<Param>();
        }
    }

    /**
     * 获取任务的名字
     * @date  Created in 2020/12/8 上午1:51
     */
    private String getTaskName(TimeWheel timeWheel){
        //小时轮
        if(timeWheel.getNum() == 48){
            return "loadNextDayTask";
        }else if(timeWheel.getNum() == 120){ //分钟轮
            return "loadNextHourTask";
        }else{ //秒轮
            return "loadNextMinuteTask";
        }
    }


    /**
     * 获取时间轮一次load的数量
     * @date  Created in 2020/12/8 上午1:38
     */
    private Integer getWheelListNum(TimeWheel timeWheel){
        return timeWheel.getNum()/2;
    }

    /**
     * 计算应到哪个格子
     * @date  Created in 2020/12/3 上午1:16
     */
    private Integer calculateTimeGrid(TimeWheel timeWheel, Task task){
        //小时轮
        if(timeWheel.getNum() == 48){
            return (task.getBeginTime().getHour()*2+task.getBeginTime().getMinute()/30) % 24;
        }else if(timeWheel.getNum() == 120){ //分钟轮
            return (task.getBeginTime().getMinute() * 2 + task.getBeginTime().getSecond()/30) % 60;
        }else{ //秒轮
            return task.getBeginTime().getSecond() % 30;
        }
    }

    /**
     * 计算三种任务sendTime
     * @date  Created in 2020/12/3 上午1:16
     */
    private LocalDateTime calculateSendTime(LocalDateTime loadTime, int minIndex, TimeWheel timeWheel){
        if(timeWheel.getNum() == 48){
            //计算运行时间，运行时间处于最少任务量的格子的中间时间，因而加了15分钟
            return loadTime.plusMinutes(minIndex*30 + 15);
        }else if(timeWheel.getNum() == 120){    //分钟轮
            return loadTime.plusSeconds(minIndex*30);
        }else{  //秒轮
            return loadTime.plusSeconds(minIndex);
        }
    }
}
