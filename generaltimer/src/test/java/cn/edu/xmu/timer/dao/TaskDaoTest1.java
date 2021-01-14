package cn.edu.xmu.timer.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.model.bo.Param;
import cn.edu.xmu.timer.model.bo.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @Author Pinzhen Chen
 * @Date 2020/12/2 9:35
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Transactional
public class TaskDaoTest1 {

    @Autowired
    private TaskDao taskDao;

    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @Test
    public void insertTask1() {
        LocalDateTime beginTime = LocalDateTime.parse("2021-10-02 00:00:00",df);
        Task task=new Task();
        task.setBeginTime(beginTime);
        task.setBeanName("bean1");
        task.setMethodName("m1");
        task.setReturnTypeName("r1");
        task.setTopic("ttt");
        task.setTag("g1");
        List<Param> paramList=new ArrayList<>();
        Param param1 = new Param();
        param1.setTypeName("pt1");
        param1.setSeq(6);
        param1.setParamValue("param1");
        Param param2 = new Param();
        param2.setTypeName("pt2");
        param2.setSeq(2);
        param2.setParamValue("param2");
        Param param3 = new Param();
        param3.setTypeName("pt2");
        param3.setSeq(4);
        param3.setParamValue("param3");
        paramList.add(param1);
        paramList.add(param2);
        paramList.add(param3);
        task.setParamList(new ArrayList<>(paramList));
        ReturnObject returnObject =  taskDao.createTask(task,0);
        assertEquals(0, returnObject.getCode().getCode());
    }

    @Test
    public void listTasks1() {
        ReturnObject<List<Task>> results = taskDao.getTaskByTopic("ttt", null);
        assertEquals(3, results.getData().size());
    }

    @Test
    public void listTasks2() {
        ReturnObject<List<Task>> results = taskDao.getTaskByTopic("ttt", "bbb");
        assertEquals(1, results.getData().size());
    }

}
