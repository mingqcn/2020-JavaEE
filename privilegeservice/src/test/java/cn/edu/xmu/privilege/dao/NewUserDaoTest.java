package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.PrivilegeServiceApplication;
import cn.edu.xmu.privilege.model.po.NewUserPo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * newUser DAO层测试类
 * 2 * @author: LiangJi3229
 * 3 * @date: 2020/11/10 下午8:19
 * 4
 */
@SpringBootTest(classes = PrivilegeServiceApplication.class)
@Transactional
public class NewUserDaoTest {
    @Autowired
    NewUserDao newUserDao;
    final int insertDataCount=10;

    /**
     * 主要测试布隆过滤器
     */
    @Test
    public void duplicateField(){
        NewUserPo po=new NewUserPo();
        int mobile=1387654321;
        String suffixEmail="@liangji.test";
        String userName="LiangJi";
        for(int i=0;i<insertDataCount;i++){
            po.setMobile(String.valueOf(mobile+i));
            po.setEmail(String.valueOf(mobile+i)+suffixEmail);
            po.setUserName(userName+String.valueOf(i));
            newUserDao.setBloomFilterByName("email",po);
            newUserDao.setBloomFilterByName("mobile",po);
            newUserDao.setBloomFilterByName("userName",po);

        }
        for(int i=0;i<insertDataCount;i++){
            po.setMobile(String.valueOf(mobile+i+insertDataCount));
            po.setEmail(String.valueOf(mobile+i)+suffixEmail);
            po.setUserName(userName+String.valueOf(i+insertDataCount));
            ReturnObject retObj=newUserDao.checkBloomFilter(po);
            assertEquals(732,retObj.getCode().getCode());
        }
        for(int i=0;i<insertDataCount;i++){
            po.setMobile(String.valueOf(mobile+i));
            po.setEmail(String.valueOf(mobile+i+insertDataCount)+suffixEmail);
            po.setUserName(userName+String.valueOf(i+insertDataCount));
            ReturnObject retObj=newUserDao.checkBloomFilter(po);
            assertEquals(733,retObj.getCode().getCode());
        }
        for(int i=0;i<insertDataCount;i++){
            po.setMobile(String.valueOf(mobile+i+insertDataCount));
            po.setEmail(String.valueOf(mobile+i+insertDataCount)+suffixEmail);
            po.setUserName(userName+String.valueOf(i));
            ReturnObject retObj=newUserDao.checkBloomFilter(po);
            assertEquals(731,retObj.getCode().getCode());
        }
    }
}
