package cn.edu.xmu.timer.dao;


import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import cn.edu.xmu.timer.util.TaskFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * 将需要的半天任务从数据库找出的测试
 * 谢schema和testdata.sql进行测试
 * @author cxr
 * @date 2020/12/7 13:19
 */
@Slf4j
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc    //配置模拟的MVC，这样可以不启动服务器测试
public class LoadNextDayTaskTest1 {

    @Autowired
    private TaskDao taskDao;

    private LocalDateTime beginTime;
    private LocalDateTime endTime;

    @Test
    public void loadNextDayTaskTest1() throws Exception{
        LocalDate localDate = LocalDate.of(2020,12,6);
        LocalTime time1 = LocalTime.of(0,0,0);
        LocalTime time2 = time1.plusHours(12);
        beginTime = LocalDateTime.of(localDate,time1);
        endTime = LocalDateTime.of(localDate,time2);

        ReturnObject retObject = taskDao.loadNextDayTask(beginTime,endTime);

        //获取要装载的任务
        List<Task> loadTaskList = (List<Task>)retObject.getData();
        //生成期望的答案，以便进行比较
        List<Task> expectTaskList = generateExpectedList();

        assertEquals(expectTaskList,loadTaskList);
    }

    public List<Task> generateExpectedList(){
        List<Task> expectTaskList = new ArrayList<>(16);

        //定时任务
        Task task1 = new Task();
        task1.setId(1L);
        task1.setSenderName("task1");
        task1.setBeginTime(LocalDateTime.of(2020,12,6,9,0,0));
        task1.setBeanName(TaskFactory.class.getName());
        task1.setTag("test");
        task1.setTopic("test");
        task1.setMethodName("test");
        task1.setReturnTypeName("void");
        task1.setGmtCreate(LocalDateTime.of(2020,12,3,10,0,0));
        task1.setGmtModified(null);
        task1.setPeriod((byte)0);
        task1.setParamList(new ArrayList<Param>());
        //task1.setParamList(generateParamList(task1.getId()));
        expectTaskList.add(task1);

        //定期任务（每小时）[改变beginTime变成12个任务]
        for(int i=0;i<12;i++){
            Task task2 = new Task();
            task2.setId(2L);
            task2.setSenderName("task2");
            task2.setBeginTime(LocalDateTime.of(2020,12,6,i,0,0));
            task2.setBeanName(TaskFactory.class.getName());
            task2.setTag("test");
            task2.setTopic("test");
            task2.setMethodName("test");
            task2.setReturnTypeName("void");
            task2.setGmtCreate(LocalDateTime.of(2020,12,3,10,0,0));
            task2.setGmtModified(null);
            task2.setPeriod((byte)8);
            task2.setParamList(new ArrayList<Param>());
            //task2.setParamList(generateParamList(task2.getId()));
            expectTaskList.add(task2);
        }

        //定期任务（每周）1[时间在范围内]
        Task task3 = new Task();
        task3.setId(3L);
        task3.setSenderName("task3");
        task3.setBeginTime(LocalDateTime.of(2020,12,6,10,0,0));
        task3.setBeanName(TaskFactory.class.getName());
        task3.setTag("test");
        task3.setTopic("test");
        task3.setMethodName("test");
        task3.setReturnTypeName("void");
        task3.setGmtCreate(LocalDateTime.of(2020,12,3,10,0,0));
        task3.setGmtModified(null);
        task3.setPeriod((byte)7);
        task3.setParamList(new ArrayList<Param>());
        //task3.setParamList(generateParamList(task3.getId()));
        expectTaskList.add(task3);


        //定期任务（每月）1[时间在范围内]
        Task task5 = new Task();
        task5.setId(5L);
        task5.setSenderName("task5");
        task5.setBeginTime(LocalDateTime.of(2020,12,6,4,0,0));
        task5.setBeanName(TaskFactory.class.getName());
        task5.setTag("test");
        task5.setTopic("test");
        task5.setMethodName("test");
        task5.setPeriod((byte)9);
        task5.setReturnTypeName("void");
        task5.setGmtCreate(LocalDateTime.of(2020,12,3,10,0,0));
        task5.setGmtModified(null);
        task5.setParamList(new ArrayList<Param>());
        //task5.setParamList(generateParamList(task5.getId()));
        expectTaskList.add(task5);


        //定期任务（每年）1[时间在范围内]
        Task task8 = new Task();
        task8.setId(8L);
        task8.setSenderName("task8");
        task8.setBeginTime(LocalDateTime.of(2020,12,6,4,0,0));
        task8.setBeanName(TaskFactory.class.getName());
        task8.setTag("test");
        task8.setTopic("test");
        task8.setMethodName("test");
        task8.setPeriod((byte)10);
        task8.setReturnTypeName("void");
        task8.setGmtCreate(LocalDateTime.of(2020,12,3,10,0,0));
        task8.setGmtModified(null);
        task8.setParamList(new ArrayList<Param>());
        //task8.setParamList(generateParamList(task8.getId()));
        expectTaskList.add(task8);

        return expectTaskList;
    }

    private List<Param> generateParamList(Long taskId){
        List<Param> paramList = new ArrayList<>(3);

        Param param3 = new Param();
        param3.setId(taskId*3 + 3L);
        param3.setSeq(1);
        param3.setTypeName("Integer");
        param3.setParamValue("3");
        param3.setGmtCreate(null);
        param3.setGmtModified(null);
        paramList.add(param3);

        Param param1 = new Param();
        param1.setId(taskId*3 + 1L);
        param1.setSeq(2);
        param1.setTypeName("Integer");
        param1.setParamValue("1");
        param1.setGmtCreate(null);
        param1.setGmtModified(null);
        paramList.add(param1);

        Param param2 = new Param();
        param2.setId(taskId*3 + 2L);
        param2.setSeq(3);
        param2.setTypeName("Integer");
        param2.setParamValue("2");
        param2.setGmtCreate(null);
        param2.setGmtModified(null);
        paramList.add(param2);
        return paramList;
    }
}
