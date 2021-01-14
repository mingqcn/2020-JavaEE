package cn.edu.xmu.timer.util;

import cn.edu.xmu.timer.model.po.ParamPo;
import cn.edu.xmu.timer.model.po.TaskPo;
import org.junit.platform.engine.support.hierarchical.ThrowableCollector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskFactory {
    private static TaskFactory instance =  null;

    public static TaskFactory getInstance(){
        if (instance == null){
            synchronized (TaskFactory.class){
                if (instance == null){
                    instance = new TaskFactory();
                }
            }
        }
        return instance;
    }

    public List<TaskPo> createTaskPoList() {
        List<TaskPo> taskPoList = new ArrayList<>(11);

        //定时任务
        TaskPo task1 = new TaskPo();
        task1.setId(1L);
        task1.setSenderName("task1");
        task1.setBeginTime(LocalDateTime.of(2020,12,6,9,0,0));
        task1.setBeanName(TaskFactory.class.getName());
        task1.setTag(null);
        task1.setTopic(null);
        task1.setMethodName("test");
        task1.setPeriod((byte)0);
        task1.setReturnTypeName("void");
        task1.setGmtCreate(null);
        task1.setGmtModified(null);
        taskPoList.add(task1);

        //定期任务（每小时）
        TaskPo task2 = new TaskPo();
        task2.setId(2L);
        task2.setSenderName("task2");
        task2.setBeginTime(LocalDateTime.of(2020,12,6,10,0,0));
        task2.setBeanName(TaskFactory.class.getName());
        task2.setTag(null);
        task2.setTopic(null);
        task2.setMethodName("test");
        task2.setPeriod((byte)8);
        task2.setReturnTypeName("void");
        task2.setGmtCreate(null);
        task2.setGmtModified(null);
        taskPoList.add(task2);

        //定期任务（每周）1[时间在范围内]
        TaskPo task3 = new TaskPo();
        task3.setId(3L);
        task3.setSenderName("task3");
        task3.setBeginTime(LocalDateTime.of(2020,7,5,10,0,0));
        task3.setBeanName(TaskFactory.class.getName());
        task3.setTag(null);
        task3.setTopic(null);
        task3.setMethodName("test");
        task3.setPeriod((byte)7);
        task3.setReturnTypeName("void");
        task3.setGmtCreate(null);
        task3.setGmtModified(null);
        taskPoList.add(task3);

        //定期任务（每周）2[时间不在范围内]
        TaskPo task4 = new TaskPo();
        task4.setId(4L);
        task4.setSenderName("task4");
        task4.setBeginTime(LocalDateTime.of(2020,11,5,15,0,0));
        task4.setBeanName(TaskFactory.class.getName());
        task4.setTag(null);
        task4.setTopic(null);
        task4.setMethodName("test");
        task4.setPeriod((byte)7);
        task4.setReturnTypeName("void");
        task4.setGmtCreate(null);
        task4.setGmtModified(null);
        taskPoList.add(task4);

        //定期任务（每月）1[时间在范围内]
        TaskPo task5 = new TaskPo();
        task5.setId(5L);
        task5.setSenderName("task5");
        task5.setBeginTime(LocalDateTime.of(2020,5,6,4,0,0));
        task5.setBeanName(TaskFactory.class.getName());
        task5.setTag(null);
        task5.setTopic(null);
        task5.setMethodName("test");
        task5.setPeriod((byte)9);
        task5.setReturnTypeName("void");
        task5.setGmtCreate(null);
        task5.setGmtModified(null);
        taskPoList.add(task5);

        //定期任务（每月）2[时间不在范围内，天不相等]
        TaskPo task6 = new TaskPo();
        task6.setId(6L);
        task6.setSenderName("task6");
        task6.setBeginTime(LocalDateTime.of(2020,5,9,4,0,0));
        task6.setBeanName(TaskFactory.class.getName());
        task6.setTag(null);
        task6.setTopic(null);
        task6.setMethodName("test");
        task6.setPeriod((byte)9);
        task6.setReturnTypeName("void");
        task6.setGmtCreate(null);
        task6.setGmtModified(null);
        taskPoList.add(task6);

        //定期任务（每月）3[时间不在范围内，小时过大]
        TaskPo task7 = new TaskPo();
        task7.setId(7L);
        task7.setSenderName("task7");
        task7.setBeginTime(LocalDateTime.of(2020,5,6,15,0,0));
        task7.setBeanName(TaskFactory.class.getName());
        task7.setTag(null);
        task7.setTopic(null);
        task7.setMethodName("test");
        task7.setPeriod((byte)9);
        task7.setReturnTypeName("void");
        task7.setGmtCreate(null);
        task7.setGmtModified(null);
        taskPoList.add(task7);

        //定期任务（每年）1[时间在范围内]
        TaskPo task8 = new TaskPo();
        task8.setId(8L);
        task8.setSenderName("task8");
        task8.setBeginTime(LocalDateTime.of(2019,12,6,4,0,0));
        task8.setBeanName(TaskFactory.class.getName());
        task8.setTag(null);
        task8.setTopic(null);
        task8.setMethodName("test");
        task8.setPeriod((byte)10);
        task8.setReturnTypeName("void");
        task8.setGmtCreate(null);
        task8.setGmtModified(null);
        taskPoList.add(task8);

        //定期任务（每年）2[时间不在范围内，月份不对]
        TaskPo task9 = new TaskPo();
        task9.setId(9L);
        task9.setSenderName("task9");
        task9.setBeginTime(LocalDateTime.of(2019,5,6,4,0,0));
        task9.setBeanName(TaskFactory.class.getName());
        task9.setTag(null);
        task9.setTopic(null);
        task9.setMethodName("test");
        task9.setPeriod((byte)10);
        task9.setReturnTypeName("void");
        task9.setGmtCreate(null);
        task9.setGmtModified(null);
        taskPoList.add(task9);

        //定期任务（每年）3[时间不在范围内，天不对]
        TaskPo task10 = new TaskPo();
        task10.setId(10L);
        task10.setSenderName("task10");
        task10.setBeginTime(LocalDateTime.of(2019,12,3,4,0,0));
        task10.setBeanName(TaskFactory.class.getName());
        task10.setTag(null);
        task10.setTopic(null);
        task10.setMethodName("test");
        task10.setPeriod((byte)10);
        task10.setReturnTypeName("void");
        task10.setGmtCreate(null);
        task10.setGmtModified(null);
        taskPoList.add(task10);

        //定期任务（每年）4[时间不在范围内，小时过大]
        TaskPo task11 = new TaskPo();
        task11.setId(11L);
        task11.setSenderName("task11");
        task11.setBeginTime(LocalDateTime.of(2019,12,6,16,0,0));
        task11.setBeanName(TaskFactory.class.getName());
        task11.setTag(null);
        task11.setTopic(null);
        task11.setMethodName("test");
        task11.setPeriod((byte)10);
        task11.setReturnTypeName("void");
        task11.setGmtCreate(null);
        task11.setGmtModified(null);
        taskPoList.add(task11);

        return taskPoList;
    }

    public List<ParamPo> createParamPoList() {
        List<ParamPo> paramPoList = new ArrayList<>(3);
        ParamPo param1 = new ParamPo();
        param1.setId(1L);
        param1.setSeq(2);
        param1.setTypeName("Integer");
        param1.setParamValue("1");
        param1.setGmtCreate(null);
        param1.setGmtModified(null);
        paramPoList.add(param1);

        ParamPo param2 = new ParamPo();
        param2.setId(2L);
        param2.setSeq(3);
        param2.setTypeName("Integer");
        param2.setParamValue("2");
        param2.setGmtCreate(null);
        param2.setGmtModified(null);
        paramPoList.add(param2);

        ParamPo param3 = new ParamPo();
        param3.setId(3L);
        param3.setSeq(1);
        param3.setTypeName("Integer");
        param3.setParamValue("3");
        param3.setGmtCreate(null);
        param3.setGmtModified(null);
        paramPoList.add(param3);

        return paramPoList;
    }
}
