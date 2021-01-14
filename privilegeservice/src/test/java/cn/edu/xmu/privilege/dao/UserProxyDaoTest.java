package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.bo.UserProxy;
import cn.edu.xmu.privilege.model.vo.UserProxyVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Di Han Li
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class UserProxyDaoTest {

    @Autowired
    private UserProxyDao userProxyDao;

    @Test
    public void usersProxy() {
        Long aId = 2L;
        Long bId = 1L;
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-10 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        UserProxy bo=new UserProxy(userProxyVo);
        ReturnObject returnObject =  userProxyDao.usersProxy(aId, bId, bo, Long.valueOf(0));
        assertEquals(0, returnObject.getCode().getCode());
    }

    @Test
    public void aUsersProxy() {
        Long aId = 1L;
        Long bId = 2L;
        UserProxyVo userProxyVo = new UserProxyVo();
        userProxyVo.setBeginDate("2020-10-10 00:00:00");
        userProxyVo.setEndDate("2020-10-11 00:00:00");
        UserProxy bo=new UserProxy(userProxyVo);
        ReturnObject returnObject =  userProxyDao.aUsersProxy(aId, bId, bo, Long.valueOf(0));
        assertEquals(0, returnObject.getCode().getCode());
    }

    @Test
    public void removeUserProxy() {
        Long aid= 49L;
        Long id = 1L;
        ReturnObject returnObject =  userProxyDao.removeUserProxy(id,aid);
        assertEquals(0, returnObject.getCode().getCode());
    }

    @Test
    public void listProxies() {
        ReturnObject<List> results = userProxyDao.listProxies(null, null, Long.valueOf(0));
        assertEquals(5, results.getData().size());
    }

    @Test
    public void removeAllProxies() {
        Long id = 1L;
        ReturnObject returnObject =  userProxyDao.removeAllProxies(id, Long.valueOf(0));
        assertEquals(0, returnObject.getCode().getCode());
    }

}
