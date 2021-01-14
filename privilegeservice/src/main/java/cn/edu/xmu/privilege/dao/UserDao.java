package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.privilege.mapper.RolePoMapper;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.privilege.mapper.UserProxyPoMapper;
import cn.edu.xmu.privilege.mapper.UserRolePoMapper;
import cn.edu.xmu.privilege.model.bo.*;
import cn.edu.xmu.privilege.model.po.*;
import cn.edu.xmu.privilege.model.vo.*;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cn.edu.xmu.privilege.model.po.UserProxyPo;
import cn.edu.xmu.privilege.model.po.UserProxyPoExample;
import cn.edu.xmu.privilege.model.po.UserRolePo;
import cn.edu.xmu.privilege.model.po.UserRolePoExample;
import cn.edu.xmu.privilege.model.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Ming Qiu
 * @date Created in 2020/11/1 11:48
 * Modified in 2020/11/8 0:57
 **/
@Repository
public class UserDao{

    @Autowired
    private UserPoMapper userPoMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    // 用户在Redis中的过期时间，而不是JWT的有效期
    @Value("${privilegeservice.user.expiretime}")
    private long timeout;


    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private UserProxyPoMapper userProxyPoMapper;

    @Autowired
    private UserPoMapper userMapper;

    @Autowired
    private RolePoMapper rolePoMapper;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private JavaMailSender mailSender;
    /**
     * @author yue hao
     * @param id 用户ID
     * @return 用户的权限列表
     */

    public ReturnObject<List> findPrivsByUserId(Long id, Long did) {
        //getRoleIdByUserId已经进行签名校验
        User user = getUserById(id.longValue()).getData();
        if (user == null) {//判断是否是由于用户不存在造成的
            logger.error("findPrivsByUserId: 数据库不存在该用户 userid=" + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        Long departId = user.getDepartId();
        if(departId != did) {
            logger.error("findPrivsByUserId: 店铺id不匹配 userid=" + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        List<Long> roleIds = this.getRoleIdByUserId(id);
        List<Privilege> privileges = new ArrayList<>();
        for(Long roleId: roleIds) {
            List<Privilege> rolePriv = roleDao.findPrivsByRoleId(roleId);
            privileges.addAll(rolePriv);
        }
        return new ReturnObject<>(privileges);
    }


    /**
     * 由用户名获得用户
     *
     * @param userName
     * @return
     */
    public ReturnObject<User> getUserByName(String userName) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserNameEqualTo(userName);
        List<UserPo> users = null;
        try {
            users = userPoMapper.selectByExample(example);
        } catch (DataAccessException e) {
            StringBuilder message = new StringBuilder().append("getUserByName: ").append(e.getMessage());
            logger.error(message.toString());
        }

        if (null == users || users.isEmpty()) {
            return new ReturnObject<>();
        } else {
            User user = new User(users.get(0));
            if (!user.authetic()) {
                StringBuilder message = new StringBuilder().append("getUserByName: ").append("id= ")
                        .append(user.getId()).append(" username=").append(user.getUserName());
                logger.error(message.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
            } else {
                return new ReturnObject<>(user);
            }
        }
    }

    /**
     * @param userId 用户ID
     * @param IPAddr IP地址
     * @param date   登录时间
     * @return 是否成功更新
     */
    public Boolean setLoginIPAndPosition(Long userId, String IPAddr, LocalDateTime date) {
        UserPo userPo = new UserPo();
        userPo.setId(userId);
        userPo.setLastLoginIp(IPAddr);
        userPo.setLastLoginTime(date);
        if (userPoMapper.updateByPrimaryKeySelective(userPo) == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 取消用户角色
     * @param userid 用户id
     * @param roleid 角色id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> revokeRole(Long userid, Long roleid){
        UserRolePoExample userRolePoExample = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = userRolePoExample.createCriteria();
        criteria.andUserIdEqualTo(userid);
        criteria.andRoleIdEqualTo(roleid);

        User user = getUserById(userid.longValue()).getData();
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);

        //用户id或角色id不存在
        if (user == null || rolePo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        try {
            int state = userRolePoMapper.deleteByExample(userRolePoExample);
            if (state == 0){
                logger.warn("revokeRole: 未找到该用户角色");
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }


        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }

        //清除缓存
        clearUserPrivCache(userid);

        return new ReturnObject<>();
    }

    /**
     * 赋予用户角色
     * @param createid 创建者id
     * @param userid 用户id
     * @param roleid 角色id
     * @return ReturnObject<VoObject>
     * @author Xianwei Wang
     * */
    public ReturnObject<VoObject> assignRole(Long createid, Long userid, Long roleid){
        UserRolePo userRolePo = new UserRolePo();
        userRolePo.setUserId(userid);
        userRolePo.setRoleId(roleid);

        User user = getUserById(userid.longValue()).getData();
        User create = getUserById(createid.longValue()).getData();
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);

        //用户id或角色id不存在
        if (user == null || create == null || rolePo == null) {
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        userRolePo.setCreatorId(createid);
        userRolePo.setGmtCreate(LocalDateTime.now());

        UserRole userRole = new UserRole(userRolePo, user, new Role(rolePo), create);
        userRolePo.setSignature(userRole.getCacuSignature());

        //查询该用户是否已经拥有该角色
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userid);
        criteria.andRoleIdEqualTo(roleid);

        //若未拥有，则插入数据
        try {
            List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
            if (userRolePoList.isEmpty()){
                userRolePoMapper.insert(userRolePo);
            } else {
                logger.warn("assignRole: 该用户已拥有该角色 userid=" + userid + "roleid=" + roleid);
                return new ReturnObject<>(ResponseCode.USER_ROLE_REGISTERED);
            }
        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        //清除缓存
        clearUserPrivCache(userid);

        return new ReturnObject<>(new UserRole(userRolePo, user, new Role(rolePo), create));

    }

    /**
     * 使用用户id，清空该用户和被代理对象的redis缓存
     * @param userid 用户id
     * @author Xianwei Wang
     */
    private void clearUserPrivCache(Long userid){
        String key = "u_" + userid;
        redisTemplate.delete(key);

        UserProxyPoExample example = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserBIdEqualTo(userid);
        List<UserProxyPo> userProxyPoList = userProxyPoMapper.selectByExample(example);

        LocalDateTime now = LocalDateTime.now();

        for (UserProxyPo po:
             userProxyPoList) {
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //在有效期内
                    String proxyKey = "up_" + po.getUserAId();
                    redisTemplate.delete(proxyKey);
                    logger.debug("clearUserPrivCache: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                } else {
                    //代理过期了，但标志位依然是有效
                    newPo = newPo == null ? new UserProxyPo() : newPo;
                    newPo.setValid((byte) 0);
                    signature = Common.concatString("-", po.getUserAId().toString(),
                            po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                    newSignature = SHA256.getSHA256(signature.toString());
                    newPo.setSignature(newSignature);
                }
            } else {
                logger.error("clearUserPrivCache: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                logger.debug("clearUserPrivCache: writing back.. po =" + newPo);
                userProxyPoMapper.updateByPrimaryKeySelective(newPo);
            }

        }
    }

    /**
     * 获取用户的角色信息
     * @param id 用户id
     * @return UserRole列表
     * @author Xianwei Wang
     * */
    public ReturnObject<List> getUserRoles(Long id){
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
        logger.info("getUserRoles: userId = "+ id + "roleNum = "+ userRolePoList.size());

        List<UserRole> retUserRoleList = new ArrayList<>(userRolePoList.size());

        if (retUserRoleList.isEmpty()) {
            User user = getUserById(id.longValue()).getData();
            if (user == null) {
                logger.error("getUserRoles: 数据库不存在该用户 userid=" + id);
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
        }

        for (UserRolePo po : userRolePoList) {
            User user = getUserById(po.getUserId().longValue()).getData();
            User creator = getUserById(po.getCreatorId().longValue()).getData();
            RolePo rolePo = rolePoMapper.selectByPrimaryKey(po.getRoleId());
            if (user == null) {
                return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            }
            if (creator == null) {
                logger.error("getUserRoles: 数据库不存在该资源 userid=" + po.getCreatorId());
            }
            if (rolePo == null) {
                logger.error("getUserRoles: 数据库不存在该资源:rolePo id=" + po.getRoleId());
                continue;
            }

            Role role = new Role(rolePo);
            UserRole userRole = new UserRole(po, user, role, creator);

            //校验签名
            if (userRole.authetic()){
                retUserRoleList.add(userRole);
                logger.info("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
            } else {
                logger.error("getUserRoles: Wrong Signature(auth_user_role): id =" + po.getId());
            }
        }
        return new ReturnObject<>(retUserRoleList);
    }


    /**
     * @description 检查用户的departid是否与路径上的一致
     * @param userid 用户id
     * @param departid 路径上的departid
     * @return boolean
     * @author Xianwei Wang
     * created at 11/20/20 1:48 PM
     */
    public boolean checkUserDid(Long userid, Long departid) {
        UserPo userPo = userMapper.selectByPrimaryKey(userid);
        if (userPo == null) {
            return false;
        }
        if (userPo.getDepartId() != departid) {
            return false;
        }
        return true;
    }

    /**
     * @description 检查角色的departid是否与路径上的一致
     * @param roleid 角色id
     * @param departid 路径上的departid
     * @return boolean
     * @author Xianwei Wang
     * created at 11/20/20 1:51 PM
     */
    public boolean checkRoleDid(Long roleid, Long departid) {
        RolePo rolePo = rolePoMapper.selectByPrimaryKey(roleid);
        if (rolePo == null) {
            return false;
        }
        if (rolePo.getDepartId() != departid) {
            return false;
        }
        return true;
    }


    /**
     * 计算User自己的权限，load到Redis
     *
     * @param id userID
     * @return void
     * <p>
     * createdBy: Ming Qiu 2020-11-02 11:44
     * modifiedBy: Ming Qiu 2020-11-03 12:31
     * 将获取用户Roleid的代码独立, 增加redis过期时间
     * Ming Qiu 2020-11-07 8:00
     * 集合里强制加“0”
     */
    private void loadSingleUserPriv(Long id) {
        List<Long> roleIds = this.getRoleIdByUserId(id);
        String key = "u_" + id;
        Set<String> roleKeys = new HashSet<>(roleIds.size());
        for (Long roleId : roleIds) {
            String roleKey = "r_" + roleId;
            roleKeys.add(roleKey);
            if (!redisTemplate.hasKey(roleKey)) {
                roleDao.loadRolePriv(roleId);
            }
            redisTemplate.opsForSet().unionAndStore(roleKeys, key);
        }
        redisTemplate.opsForSet().add(key, 0);
        long randTimeout = Common.addRandomTime(timeout);
        redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * 获得用户的角色id
     *
     * @param id 用户id
     * @return 角色id列表
     * createdBy: Ming Qiu 2020/11/3 13:55
     */
    private List<Long> getRoleIdByUserId(Long id) {
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example);
        logger.debug("getRoleIdByUserId: userId = " + id + "roleNum = " + userRolePoList.size());
        List<Long> retIds = new ArrayList<>(userRolePoList.size());
        for (UserRolePo po : userRolePoList) {
            StringBuilder signature = Common.concatString("-",
                    po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());


            if (newSignature.equals(po.getSignature())) {
                retIds.add(po.getRoleId());
                logger.debug("getRoleIdByUserId: userId = " + po.getUserId() + " roleId = " + po.getRoleId());
            } else {
                logger.error("getRoleIdByUserId: 签名错误(auth_role_privilege): id =" + po.getId());
            }
        }
        return retIds;
    }

    /**
     * 计算User的权限（包括代理用户的权限，只计算直接代理用户），load到Redis
     *
     * @param id userID
     * @return void
     * createdBy Ming Qiu 2020/11/1 11:48
     * modifiedBy Ming Qiu 2020/11/3 14:37
     */
    public void loadUserPriv(Long id, String jwt) {

        String key = "u_" + id;
        String aKey = "up_" + id;

        List<Long> proxyIds = this.getProxyIdsByUserId(id);
        List<String> proxyUserKey = new ArrayList<>(proxyIds.size());
        for (Long proxyId : proxyIds) {
            if (!redisTemplate.hasKey("u_" + proxyId)) {
                logger.debug("loadUserPriv: loading proxy user. proxId = " + proxyId);
                loadSingleUserPriv(proxyId);
            }
            proxyUserKey.add("u_" + proxyId);
        }
        if (!redisTemplate.hasKey(key)) {
            logger.debug("loadUserPriv: loading user. id = " + id);
            loadSingleUserPriv(id);
        }
        redisTemplate.opsForSet().unionAndStore(key, proxyUserKey, aKey);
        redisTemplate.opsForSet().add(aKey, jwt);
        long randTimeout = Common.addRandomTime(timeout);
        redisTemplate.expire(aKey, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * 获得代理的用户id列表
     *
     * @param id 用户id
     * @return 被代理的用户id
     * createdBy Ming Qiu 14:37
     */
    private List<Long> getProxyIdsByUserId(Long id) {
        UserProxyPoExample example = new UserProxyPoExample();
        //查询当前所有有效的被代理用户
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserAIdEqualTo(id);
        criteria.andValidEqualTo((byte) 1);
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(userProxyPos.size());
        LocalDateTime now = LocalDateTime.now();
        for (UserProxyPo po : userProxyPos) {
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            UserProxyPo newPo = null;

            if (newSignature.equals(po.getSignature())) {
                if (now.isBefore(po.getEndDate()) && now.isAfter(po.getBeginDate())) {
                    //在有效期内
                    retIds.add(po.getUserBId());
                    logger.debug("getProxyIdsByUserId: userAId = " + po.getUserAId() + " userBId = " + po.getUserBId());
                } else {
                    //代理过期了，但标志位依然是有效
                    newPo = newPo == null ? new UserProxyPo() : newPo;
                    newPo.setValid((byte) 0);
                    signature = Common.concatString("-", po.getUserAId().toString(),
                            po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), newPo.getValid().toString());
                    newSignature = SHA256.getSHA256(signature.toString());
                    newPo.setSignature(newSignature);
                }
            } else {
                logger.error("getProxyIdsByUserId: Wrong Signature(auth_user_proxy): id =" + po.getId());
            }

            if (null != newPo) {
                logger.debug("getProxyIdsByUserId: writing back.. po =" + newPo);
                userProxyPoMapper.updateByPrimaryKeySelective(newPo);
            }
        }
        return retIds;
    }

    public void initialize() throws Exception {
        //初始化user
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andSignatureIsNull();

        List<UserPo> userPos = userMapper.selectByExample(example);

        for (UserPo po : userPos) {
            UserPo newPo = new UserPo();
            newPo.setPassword(AES.encrypt(po.getPassword(), User.AESPASS));
            newPo.setEmail(AES.encrypt(po.getEmail(), User.AESPASS));
            newPo.setMobile(AES.encrypt(po.getMobile(), User.AESPASS));
            newPo.setName(AES.encrypt(po.getName(), User.AESPASS));
            newPo.setId(po.getId());

            StringBuilder signature = Common.concatString("-", po.getUserName(), newPo.getPassword(),
                    newPo.getMobile(), newPo.getEmail(), po.getOpenId(), po.getState().toString(), po.getDepartId().toString(),
                    po.getCreatorId().toString());
            newPo.setSignature(SHA256.getSHA256(signature.toString()));

            userMapper.updateByPrimaryKeySelective(newPo);
        }

        //初始化UserProxy
        UserProxyPoExample example1 = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andSignatureIsNull();
        List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example1);

        for (UserProxyPo po : userProxyPos) {
            UserProxyPo newPo = new UserProxyPo();
            newPo.setId(po.getId());
            StringBuilder signature = Common.concatString("-", po.getUserAId().toString(),
                    po.getUserBId().toString(), po.getBeginDate().toString(), po.getEndDate().toString(), po.getValid().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            newPo.setSignature(newSignature);
            userProxyPoMapper.updateByPrimaryKeySelective(newPo);
        }

        //初始化UserRole
        UserRolePoExample example3 = new UserRolePoExample();
        UserRolePoExample.Criteria criteria3 = example3.createCriteria();
        criteria3.andSignatureIsNull();
        List<UserRolePo> userRolePoList = userRolePoMapper.selectByExample(example3);
        for (UserRolePo po : userRolePoList) {
            StringBuilder signature = Common.concatString("-",
                    po.getUserId().toString(), po.getRoleId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());

            UserRolePo newPo = new UserRolePo();
            newPo.setId(po.getId());
            newPo.setSignature(newSignature);
            userRolePoMapper.updateByPrimaryKeySelective(newPo);
        }

    }

    /**
     * 获得用户
     *
     * @param id userID
     * @return User
     * createdBy 3218 2020/11/4 15:48
     * modifiedBy 3218 2020/11/4 15:48
     */

    public ReturnObject<User> getUserById(Long id) {
        UserPo userPo = userMapper.selectByPrimaryKey(id);
        if (userPo == null) {
            return new ReturnObject(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        User user = new User(userPo);
        if (!user.authetic()) {
            StringBuilder message = new StringBuilder().append("getUserById: ").append(ResponseCode.RESOURCE_FALSIFY.getMessage()).append(" id = ")
                    .append(user.getId()).append(" username =").append(user.getUserName());
            logger.error(message.toString());
            return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
        }
        return new ReturnObject<>(user);
    }


    /**
     * 更新用户图片
     *
     * @param user
     * @return User
     * createdBy 3218 2020/11/4 15:55
     * modifiedBy 3218 2020/11/4 15:55
     */
    public ReturnObject updateUserAvatar(User user) {
        ReturnObject returnObject = new ReturnObject();
        UserPo newUserPo = new UserPo();
        newUserPo.setId(user.getId());
        newUserPo.setAvatar(user.getAvatar());
        int ret = userMapper.updateByPrimaryKeySelective(newUserPo);
        if (ret == 0) {
            logger.debug("updateUserAvatar: update fail. user id: " + user.getId());
            returnObject = new ReturnObject(ResponseCode.FIELD_NOTVALID);
        } else {
            logger.debug("updateUserAvatar: update user success : " + user.toString());
            returnObject = new ReturnObject();
        }
        return returnObject;
    }

    /**
     * ID获取用户信息
     * @author XQChen
     * @param id
     * @return 用户
     */
    public UserPo findUserById(Long Id) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(Id);

        logger.debug("findUserById: Id =" + Id);
        UserPo userPo = userPoMapper.selectByPrimaryKey(Id);

        return userPo;
    }

    /**
     * ID获取用户信息
     * @author XQChen
     * @param id
     * @param did
     * @return 用户
     */
    public UserPo findUserByIdAndDid(Long Id, Long did) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andIdEqualTo(Id);
        criteria.andDepartIdEqualTo(did);

        logger.debug("findUserByIdAndDid: Id =" + Id + " did = " + did);
        UserPo userPo = userPoMapper.selectByPrimaryKey(Id);

        return userPo;
    }

    /**
     * 获取所有用户信息
     * @author XQChen
     * @return List<UserPo> 用户列表
     */
    public PageInfo<UserPo> findAllUsers(String userNameAES, String mobileAES, Long did) {
        UserPoExample example = new UserPoExample();
        UserPoExample.Criteria criteria = example.createCriteria();
        criteria.andDepartIdEqualTo(did);
        if(!userNameAES.isBlank())
            criteria.andUserNameEqualTo(userNameAES);
        if(!mobileAES.isBlank())
            criteria.andMobileEqualTo(mobileAES);

        List<UserPo> users = userPoMapper.selectByExample(example);

        logger.debug("findUserById: retUsers = "+users);

        return new PageInfo<>(users);
    }

    /* auth009 */

    /**
     * 根据 id 修改用户信息
     *
     * @param userVo 传入的 User 对象
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> modifyUserByVo(Long id, UserVo userVo) {
        // 查询密码等资料以计算新签名
        UserPo orig = userMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户
        if (orig == null || (orig.getState() != null && User.State.getTypeByCode(orig.getState().intValue()) == User.State.DELETE)) {
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        // 构造 User 对象以计算签名
        User user = new User(orig);
        UserPo po = user.createUpdatePo(userVo);

        // 将更改的联系方式 (如发生变化) 的已验证字段改为 false
        if (userVo.getEmail() != null && !userVo.getEmail().equals(user.getEmail())) {
            po.setEmailVerified((byte) 0);
        }
        if (userVo.getMobile() != null && !userVo.getMobile().equals(user.getMobile())) {
            po.setMobileVerified((byte) 0);
        }

        // 更新数据库
        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = userMapper.updateByPrimaryKeySelective(po);
        } catch (DataAccessException e) {
            // 如果发生 Exception，判断是邮箱还是啥重复错误
            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.auth_user_mobile_uindex")) {
                logger.info("电话重复：" + userVo.getMobile());
                retObj = new ReturnObject<>(ResponseCode.MOBILE_REGISTERED);
            } else if (e.getMessage().contains("auth_user.auth_user_email_uindex")) {
                logger.info("邮箱重复：" + userVo.getEmail());
                retObj = new ReturnObject<>(ResponseCode.EMAIL_REGISTERED);
            } else {
                // 其他情况属未知错误
                logger.error("数据库错误：" + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                        String.format("发生了严重的数据库错误：%s", e.getMessage()));
            }
            return retObj;
        } catch (Exception e) {
            // 其他 Exception 即属未知错误
            logger.error("严重错误：" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        // 检查更新有否成功
        if (ret == 0) {
            logger.info("用户不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("用户 id = " + id + " 的资料已更新");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * (物理) 删除用户
     *
     * @param id 用户 id
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> physicallyDeleteUser(Long id) {
        ReturnObject<Object> retObj;
        int ret = userMapper.deleteByPrimaryKey(id);
        if (ret == 0) {
            logger.info("用户不存在或已被删除：id = " + id);
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        } else {
            logger.info("用户 id = " + id + " 已被永久删除");
            retObj = new ReturnObject<>();
        }
        return retObj;
    }

    /**
     * 创建可改变目标用户状态的 Po
     *
     * @param id    用户 id
     * @param state 用户目标状态
     * @return UserPo 对象
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    private UserPo createUserStateModPo(Long id, User.State state) {
        // 查询密码等资料以计算新签名
        UserPo orig = userMapper.selectByPrimaryKey(id);
        // 不修改已被逻辑废弃的账户的状态
        if (orig == null || (orig.getState() != null && User.State.getTypeByCode(orig.getState().intValue()) == User.State.DELETE)) {
            return null;
        }

        // 构造 User 对象以计算签名
        User user = new User(orig);
        user.setState(state);
        // 构造一个全为 null 的 vo 因为其他字段都不用更新
        UserVo vo = new UserVo();

        return user.createUpdatePo(vo);
    }

    /**
     * 改变用户状态
     *
     * @param id    用户 id
     * @param state 目标状态
     * @return 返回对象 ReturnObj
     * @author 19720182203919 李涵
     * Created at 2020/11/4 20:30
     * Modified by 19720182203919 李涵 at 2020/11/5 10:42
     */
    public ReturnObject<Object> changeUserState(Long id, User.State state) {
        UserPo po = createUserStateModPo(id, state);
        if (po == null) {
            logger.info("用户不存在或已被删除：id = " + id);
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Object> retObj;
        int ret;
        try {
            ret = userMapper.updateByPrimaryKeySelective(po);
            if (ret == 0) {
                logger.info("用户不存在或已被删除：id = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
            } else {
                logger.info("用户 id = " + id + " 的状态修改为 " + state.getDescription());
                retObj = new ReturnObject<>();
            }
        } catch (DataAccessException e) {
            // 数据库错误
            logger.error("数据库错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的数据库错误：%s", e.getMessage()));
        } catch (Exception e) {
            // 属未知错误
            logger.error("严重错误：" + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,
                    String.format("发生了严重的未知错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /* auth009 ends */

        /* auth002 begin*/

    /**
     * auth002: 用户重置密码
     * @param vo 重置密码对象
     * @param ip 请求ip地址
     * @author 24320182203311 杨铭
     * Created at 2020/11/11 19:32
     */
    public ReturnObject<Object> resetPassword(ResetPwdVo vo, String ip) {

        //防止重复请求验证码
        if(redisTemplate.hasKey("ip_"+ip))
            return new ReturnObject<>(ResponseCode.AUTH_USER_FORBIDDEN);
        else {
            //1 min中内不能重复请求
            redisTemplate.opsForValue().set("ip_"+ip,ip);
            redisTemplate.expire("ip_" + ip, 60*1000, TimeUnit.MILLISECONDS);
        }

        //验证邮箱、手机号
        UserPoExample userPoExample1 = new UserPoExample();
        UserPoExample.Criteria criteria = userPoExample1.createCriteria();
        criteria.andMobileEqualTo(AES.encrypt(vo.getMobile(),User.AESPASS));
        List<UserPo> userPo1 = null;
        try {
            userPo1 = userMapper.selectByExample(userPoExample1);
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        if(userPo1.isEmpty())
            return new ReturnObject<>(ResponseCode.MOBILE_WRONG);
        else if(!userPo1.get(0).getEmail().equals(AES.encrypt(vo.getEmail(), User.AESPASS)))
            return new ReturnObject<>(ResponseCode.EMAIL_WRONG);


        //随机生成验证码
        String captcha = RandomCaptcha.getRandomString(6);
        while(redisTemplate.hasKey(captcha))
            captcha = RandomCaptcha.getRandomString(6);

        String id = userPo1.get(0).getId().toString();
        String key = "cp_" + captcha;
        //key:验证码,value:id存入redis
        redisTemplate.opsForValue().set(key,id);
        //五分钟后过期
        redisTemplate.expire("cp_" + captcha, 5*60*1000, TimeUnit.MILLISECONDS);


//        //发送邮件(请在配置文件application.properties填写密钥)
//        SimpleMailMessage msg = new SimpleMailMessage();
//        msg.setSubject("【oomall】密码重置通知");
//        msg.setSentDate(new Date());
//        msg.setText("您的验证码是：" + captcha + "，5分钟内有效。");
//        msg.setFrom("925882085@qq.com");
//        msg.setTo(vo.getEmail());
//        try {
//            mailSender.send(msg);
//        } catch (MailException e) {
//            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
//        }

        return new ReturnObject<>(ResponseCode.OK);
    }

    /**
     * auth002: 用户修改密码
     * @param modifyPwdVo 修改密码对象
     * @return Object
     * @author 24320182203311 杨铭
     * Created at 2020/11/11 19:32
     */
    public ReturnObject<Object> modifyPassword(ModifyPwdVo modifyPwdVo) {


        //通过验证码取出id
        if(!redisTemplate.hasKey("cp_"+modifyPwdVo.getCaptcha()))
            return new ReturnObject<>(ResponseCode.AUTH_INVALID_ACCOUNT);
        String id= redisTemplate.opsForValue().get("cp_"+modifyPwdVo.getCaptcha()).toString();

        UserPo userpo = null;
        try {
            userpo = userPoMapper.selectByPrimaryKey(Long.parseLong(id));
        }catch (Exception e) {
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }

        //新密码与原密码相同
        if(AES.decrypt(userpo.getPassword(), User.AESPASS).equals(modifyPwdVo.getNewPassword()))
            return new ReturnObject<>(ResponseCode.PASSWORD_SAME);

        //加密
        UserPo userPo = new UserPo();
        userPo.setPassword(AES.encrypt(modifyPwdVo.getNewPassword(),User.AESPASS));

        //更新数据库
        try {
            userMapper.updateByPrimaryKeySelective(userPo);
        }catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR,e.getMessage());
        }
        return new ReturnObject<>(ResponseCode.OK);
    }

    /* auth002 end*/


    /**
     * 清除缓存中的与role关联的user
     *
     * @param id 角色id
     * createdBy 王琛 24320182203277
     */
    public void clearUserByRoleId(Long id){
        UserRolePoExample example = new UserRolePoExample();
        UserRolePoExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);

        List<UserRolePo> userrolePos = userRolePoMapper.selectByExample(example);
        Long uid;
        for(UserRolePo e:userrolePos){
            uid = e.getUserId();
            clearUserPrivCache(uid);
        }
    }
     /**
     * 创建user
     *
     * createdBy Li Zihan 243201822032227
     */
    public ReturnObject addUser(NewUserPo po)
    {
        ReturnObject returnObject = null;
        UserPo userPo = new UserPo();
        userPo.setEmail(AES.encrypt(po.getEmail(), User.AESPASS));
        userPo.setMobile(AES.encrypt(po.getMobile(), User.AESPASS));
        userPo.setUserName(po.getUserName());
        userPo.setAvatar(po.getAvatar());
        userPo.setDepartId(po.getDepartId());
        userPo.setOpenId(po.getOpenId());
        userPo.setGmtCreate(LocalDateTime.now());
        try{
            returnObject = new ReturnObject<>(userPoMapper.insert(userPo));
            logger.debug("success insert User: " + userPo.getId());
        }
        catch (DataAccessException e)
        {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_user.user_name_uindex")) {
                //若有重复名则修改失败
                logger.debug("insertUser: have same user name = " + userPo.getName());
                returnObject = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("用户名重复：" + userPo.getName()));
            } else {
                logger.debug("sql exception : " + e.getMessage());
                returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }

        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            returnObject = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return returnObject;
    }

    /**
     * 功能描述: 修改用户depart
     * @Param: userId departId
     * @Return:
     * @Author: Yifei Wang
     * @Date: 2020/12/8 11:35
     */
    public ReturnObject changeUserDepart(Long userId, Long departId){
        UserPo po = new UserPo();
        po.setId(userId);
        po.setDepartId(departId);
        try{
            logger.debug("Update User: " + userId);
            int ret=userPoMapper.updateByPrimaryKeySelective(po);
            if(ret == 0){
                return new ReturnObject<>(ResponseCode.FIELD_NOTVALID);
            }
            logger.debug("Success Update User: " + userId);
            return new ReturnObject<>(ResponseCode.OK);
        }catch (Exception e){
            logger.error("exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR);
        }
    }
}

