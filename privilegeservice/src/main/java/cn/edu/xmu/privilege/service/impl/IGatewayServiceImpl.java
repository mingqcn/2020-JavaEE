package cn.edu.xmu.privilege.service.impl;

import cn.edu.xmu.privilegeservice.client.IGatewayService;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.dao.UserDao;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @title IGatewayServiceImpl.java
 * @description 网关内部调用实现
 * @author wwc
 * @date 2020/12/01 23:17
 * @version 1.0
 */
@DubboService // 注意这里的Serivce引用的是dubbo的包
public class IGatewayServiceImpl implements IGatewayService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Override
    public void loadSingleUserPriv(Long userId, String jwt) {
        userDao.loadUserPriv(userId, jwt);
    }

}
