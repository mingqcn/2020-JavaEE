package cn.edu.xmu.log.dao;

import cn.edu.xmu.log.LogServiceApplication;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Di Han Li
 **/
@SpringBootTest(classes = LogServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class LogDaoTest {

    @Autowired
    private LogDao logDao;

    @Test
    public void deleteLogs() {
        Long departId = 1L;
        LogVo logVo = new LogVo();
        logVo.setBeginTime("2020-10-10 00:00:00");
        logVo.setEndTime("2020-10-11 00:00:00");
        Log bo = new Log(logVo);
        ReturnObject returnObject = logDao.deleteLogs(bo, departId);
        assertEquals(0, returnObject.getCode().getCode());
    }


}
