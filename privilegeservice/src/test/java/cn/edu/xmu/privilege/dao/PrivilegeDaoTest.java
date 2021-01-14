package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.bo.Privilege;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ming Qiu
 **/
@SpringBootTest(classes = PrivilegeServiceApplication.class)   //标识本类是一个SpringBootTest
@Transactional
public class PrivilegeDaoTest {

    @Autowired
    private PrivilegeDao privilegeDao;

    
    @Test
    public void getPrivIdByKey(){
        Long p1 = privilegeDao.getPrivIdByKey("/adminusers/{id}", Privilege.RequestType.GET);
        assertEquals(2, p1);

        p1 = privilegeDao.getPrivIdByKey("/adminusers/{id}", Privilege.RequestType.DELETE);
        assertEquals(4, p1);
    }

}
