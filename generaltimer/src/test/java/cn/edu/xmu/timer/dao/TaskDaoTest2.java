package cn.edu.xmu.timer.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.timer.Application;
import cn.edu.xmu.timer.mapper.ParamPoMapper;
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
 * @Author Li Dihan
 * @Date 2020/12/2 9:35
 */
@SpringBootTest(classes = Application.class)   //标识本类是一个SpringBootTest
@Transactional
public class TaskDaoTest2 {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TaskDao taskDao;

    @Test
    public void removeTask1() {
        Long id=1L;
        ReturnObject returnObject=taskDao.removeTask(id);
        assertEquals(0, returnObject.getCode().getCode());
    }

    @Test
    public void cleanTaskByTopic1() {
        ReturnObject returnObject=taskDao.cleanTaskByTopic("11","33");
        List<Long> list=new ArrayList<>();
        list.add(2L);
        list.add(3L);
        assertEquals(list, returnObject.getData());
    }

    @Test
    public void cleanTaskByTopic2() {
        ReturnObject returnObject=taskDao.cleanTaskByTopic("44","33");
        List<Long> list=new ArrayList<>();
        list.add(10L);
        assertEquals(list, returnObject.getData());
    }

    @Test
    public void cleanTaskByTopic3() {
        ReturnObject returnObject=taskDao.cleanTaskByTopic("66","33");
        List<Long> list=new ArrayList<>();
        list.add(1L);
        assertEquals(list, returnObject.getData());
    }

    @Test
    public void cleanTaskByTopic4() {
        ReturnObject returnObject=taskDao.cleanTaskByTopic("88","33");
        List<Long> list=new ArrayList<>();
        list.add(9L);
        assertEquals(list, returnObject.getData());
    }

    @Test
    public void cleanTaskByTopic5() {
        ReturnObject returnObject=taskDao.cleanTaskByTopic("99","33");
        List<Long> list=new ArrayList<>();
        list.add(11L);
        list.add(14L);
        assertEquals(list, returnObject.getData());
    }

    @Test
    public void cleanTaskByTopic6() {
        ReturnObject returnObject=taskDao.cleanTaskByTopic("22",null);
        List<Long> list=new ArrayList<>();
        list.add(4L);
        list.add(5L);
        list.add(6L);
        list.add(7L);
        list.add(12L);
        list.add(13L);
        assertEquals(list, returnObject.getData());
    }



}
