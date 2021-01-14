package cn.edu.xmu.privilege.service;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.UserProxyDao;
import cn.edu.xmu.privilege.model.bo.UserProxy;
import cn.edu.xmu.privilege.model.vo.UserProxyVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务
 *
 * @author Di Han Li
 * Modified by 24320182203221 李狄翰 at 2020/11/8 8:00
 **/
@Service
public class UserProxyService {

    private Logger logger = LoggerFactory.getLogger(UserProxyService.class);

    @Autowired
    private UserProxyDao userProxyDao;

    public ReturnObject usersProxy(Long userId, Long id, UserProxyVo vo,Long departid) {
        UserProxy bo=new UserProxy(vo);
        return userProxyDao.usersProxy(userId, id, bo,departid);
    }

    public ReturnObject aUsersProxy(Long aid, Long bid, UserProxyVo vo,Long departid) {
        UserProxy bo=new UserProxy(vo);
        return userProxyDao.aUsersProxy(aid, bid, bo,departid);
    }

    public ReturnObject removeUserProxy(Long id, Long userId) {
        return userProxyDao.removeUserProxy(id, userId);
    }

    public ReturnObject listProxies(Long aId, Long bId,Long did) {
        return userProxyDao.listProxies(aId, bId,did);
    }

    public ReturnObject removeAllProxies(Long id,Long did) {
        return userProxyDao.removeAllProxies(id,did);
    }
}

