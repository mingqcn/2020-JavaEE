package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.privilege.mapper.*;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.bo.RolePrivilege;
import cn.edu.xmu.privilege.model.po.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 角色访问类
 * @author Ming Qiu
 * createdBy Ming Qiu 2020/11/02 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Repository
public class RoleDao {

    private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

    @Value("${privilegeservice.role.expiretime}")
    private long timeout;

    @Autowired
    private RolePoMapper roleMapper;

    @Autowired
    private UserPoMapper userMapper;

    @Autowired
    private PrivilegePoMapper privilegePoMapper;

    @Autowired
    private UserRolePoMapper userRolePoMapper;

    @Autowired
    private UserProxyPoMapper userProxyPoMapper;

    @Autowired
    private RolePrivilegePoMapper rolePrivilegePoMapper;

    @Autowired
    private PrivilegeDao privDao;

    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 根据角色Id,查询角色的所有权限
     * @author yue hao
     * @param id 角色ID
     * @return 角色的权限列表
     */
    public List<Privilege> findPrivsByRoleId(Long id) {
        //getPrivIdsByRoleId已经进行role的签名校验
        List<Long> privIds = this.getPrivIdsByRoleId(id);
        List<Privilege> privileges = new ArrayList<>();
        for(Long privId: privIds) {
            Privilege po = this.privDao.findPriv(privId);
            logger.debug("findPrivsByRoleId:  po = " + po);
            privileges.add(po);
        }
        return privileges;
    }

    /**
     * 将一个角色的所有权限id载入到Redis
     *
     * @param id 角色id
     * @return void
     *
     * createdBy: Ming Qiu 2020-11-02 11:44
     * ModifiedBy: Ming Qiu 2020-11-03 12:24
     * 将读取权限id的代码独立为getPrivIdsByRoleId. 增加redis值的有效期
     *            Ming Qiu 2020-11-07 8:00
     * 集合里强制加“0”
     */
    public void loadRolePriv(Long id) {
        List<Long> privIds = this.getPrivIdsByRoleId(id);
        String key = "r_" + id;
        for (Long pId : privIds) {
            redisTemplate.opsForSet().add(key, pId);
        }
        redisTemplate.opsForSet().add(key,0);
        long randTimeout = Common.addRandomTime(this.timeout);
        redisTemplate.expire(key, randTimeout, TimeUnit.SECONDS);
    }

    /**
     * 由Role Id 获得 Privilege Id 列表
     *
     * @param id: Role id
     * @return Privilege Id 列表
     * created by Ming Qiu in 2020/11/3 11:48
     */
    private List<Long> getPrivIdsByRoleId(Long id) {
        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
        criteria.andRoleIdEqualTo(id);
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(rolePrivilegePos.size());
        for (RolePrivilegePo po : rolePrivilegePos) {
            StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());

            if (newSignature.equals(po.getSignature())) {
                retIds.add(po.getPrivilegeId());
                logger.debug("getPrivIdsBByRoleId: roleId = " + po.getRoleId() + " privId = " + po.getPrivilegeId());
            } else {
                logger.info("getPrivIdsBByRoleId: Wrong Signature(auth_role_privilege): id =" + po.getId());
            }
        }
        return retIds;
    }

    public void initialize() throws Exception {
        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
        criteria.andSignatureIsNull();
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        List<Long> retIds = new ArrayList<>(rolePrivilegePos.size());
        for (RolePrivilegePo po : rolePrivilegePos) {
            StringBuilder signature = Common.concatString("-", po.getRoleId().toString(),
                    po.getPrivilegeId().toString(), po.getCreatorId().toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            RolePrivilegePo newPo = new RolePrivilegePo();
            newPo.setId(po.getId());
            newPo.setSignature(newSignature);
            rolePrivilegePoMapper.updateByPrimaryKeySelective(newPo);
        }

    }

    /**
     * 分页查询所有角色
     *
     * @author 24320182203281 王纬策
     * @param pageNum 页数
     * @param pageSize 每页大小
     * @return ReturnObject<List> 角色列表
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public ReturnObject<PageInfo<VoObject>> selectAllRole(Long departId, Integer pageNum, Integer pageSize) {
        RolePoExample example = new RolePoExample();
        RolePoExample.Criteria criteria = example.createCriteria();
        criteria.andDepartIdEqualTo(departId);
        //分页查询
        PageHelper.startPage(pageNum, pageSize);
        logger.debug("page = " + pageNum + "pageSize = " + pageSize);
        List<RolePo> rolePos = null;
        try {
            //不加限定条件查询所有
            rolePos = roleMapper.selectByExample(example);
            List<VoObject> ret = new ArrayList<>(rolePos.size());
            for (RolePo po : rolePos) {
                Role role = new Role(po);
                ret.add(role);
            }
            PageInfo<VoObject> rolePage = PageInfo.of(ret);
            return new ReturnObject<>(rolePage);
        }
        catch (DataAccessException e){
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 增加一个角色
     *
     * @author 24320182203281 王纬策
     * @param role 角色bo
     * @return ReturnObject<Role> 新增结果
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public ReturnObject<Role> insertRole(Role role) {
        RolePo rolePo = role.gotRolePo();
        ReturnObject<Role> retObj = null;
        try{
            int ret = roleMapper.insertSelective(rolePo);
            if (ret == 0) {
                //插入失败
                logger.debug("insertRole: insert role fail " + rolePo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("新增失败：" + rolePo.getName()));
            } else {
                //插入成功
                logger.debug("insertRole: insert role = " + rolePo.toString());
                role.setId(rolePo.getId());
                retObj = new ReturnObject<>(role);
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
                //若有重复的角色名则新增失败
                logger.debug("updateRole: have same role name = " + rolePo.getName());
                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + rolePo.getName()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 删除一个角色
     *
     * @author 24320182203281 王纬策
     * @param id 角色id
     * @return ReturnObject<Object> 删除结果
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public ReturnObject<Object> deleteRole(Long did, Long id) {
        ReturnObject<Object> retObj = null;
        RolePoExample rolePoDid= new RolePoExample();
        RolePoExample.Criteria criteriaDid = rolePoDid.createCriteria();
        criteriaDid.andIdEqualTo(id);
        criteriaDid.andDepartIdEqualTo(did);
        try {
            int ret = roleMapper.deleteByExample(rolePoDid);
            if (ret == 0) {
                //删除角色表
                logger.debug("deleteRole: id not exist = " + id);
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + id));
            } else {
                //删除角色权限表
                logger.debug("deleteRole: delete role id = " + id);
                RolePrivilegePoExample exampleRP = new RolePrivilegePoExample();
                RolePrivilegePoExample.Criteria criteriaRP = exampleRP.createCriteria();
                criteriaRP.andRoleIdEqualTo(id);
                List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(exampleRP);
                logger.debug("deleteRole: delete role-privilege num = " + rolePrivilegePos.size());
                for (RolePrivilegePo rolePrivilegePo : rolePrivilegePos) {
                    rolePrivilegePoMapper.deleteByPrimaryKey(rolePrivilegePo.getId());
                }
                //删除缓存中角色权限信息
                redisTemplate.delete("r_" + id);
                //删除用户角色表
                UserRolePoExample exampleUR = new UserRolePoExample();
                UserRolePoExample.Criteria criteriaUR = exampleUR.createCriteria();
                criteriaUR.andRoleIdEqualTo(id);
                List<UserRolePo> userRolePos = userRolePoMapper.selectByExample(exampleUR);
                logger.debug("deleteRole: delete user-role num = " + userRolePos.size());
                for (UserRolePo userRolePo : userRolePos) {
                    userRolePoMapper.deleteByPrimaryKey(userRolePo.getId());
                    //删除缓存中具有删除角色的用户权限
                    redisTemplate.delete("u_" + userRolePo.getUserId());
                    redisTemplate.delete("up_" + userRolePo.getUserId());
                    //查询当前所有有效的代理具有删除角色用户的代理用户
                    UserProxyPoExample example = new UserProxyPoExample();
                    UserProxyPoExample.Criteria criteria = example.createCriteria();
                    criteria.andUserBIdEqualTo(userRolePo.getUserId());
                    List<UserProxyPo> userProxyPos = userProxyPoMapper.selectByExample(example);
                    for(UserProxyPo userProxyPo : userProxyPos){
                        //删除缓存中代理了具有删除角色的用户的代理用户
                        redisTemplate.delete("u_" + userProxyPo.getUserAId());
                        redisTemplate.delete("up_" + userProxyPo.getUserAId());
                    }
                }
                retObj = new ReturnObject<>();
            }

            return retObj;
        }
        catch (DataAccessException e){
            logger.error("selectAllRole: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }

    /**
     * 修改一个角色
     *
     * @author 24320182203281 王纬策
     * @param role 角色bo
     * @return ReturnObject<Role> 修改结果
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public ReturnObject<Role> updateRole(Role role) {
        RolePo rolePo = role.gotRolePo();
        ReturnObject<Role> retObj = null;
        RolePoExample rolePoExample = new RolePoExample();
        RolePoExample.Criteria criteria = rolePoExample.createCriteria();
        criteria.andIdEqualTo(role.getId());
        criteria.andDepartIdEqualTo(role.getDepartId());
        try{
            int ret = roleMapper.updateByExampleSelective(rolePo, rolePoExample);
//            int ret = roleMapper.updateByPrimaryKeySelective(rolePo);
            if (ret == 0) {
                //修改失败
                logger.debug("updateRole: update role fail : " + rolePo.toString());
                retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST, String.format("角色id不存在：" + rolePo.getId()));
            } else {
                //修改成功
                logger.debug("updateRole: update role = " + rolePo.toString());
                retObj = new ReturnObject<>();
            }
        }
        catch (DataAccessException e) {
            if (Objects.requireNonNull(e.getMessage()).contains("auth_role.auth_role_name_uindex")) {
                //若有重复的角色名则修改失败
                logger.debug("updateRole: have same role name = " + rolePo.getName());
                retObj = new ReturnObject<>(ResponseCode.ROLE_REGISTERED, String.format("角色名重复：" + rolePo.getName()));
            } else {
                // 其他数据库错误
                logger.debug("other sql exception : " + e.getMessage());
                retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            retObj = new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
        return retObj;
    }

    /**
     * 由Role Id, Privilege Id 增加 角色权限
     *
     * @param  roleid, Privilegeid, userid
     * @return RolePrivilegeRetVo
     * created by 王琛 24320182203277
     */
    public ReturnObject<VoObject> addPrivByRoleIdAndPrivId(Long roleid, Long privid, Long userid){
        UserPo userpo = userMapper.selectByPrimaryKey(userid);
        PrivilegePo privilegepo = privilegePoMapper.selectByPrimaryKey(privid);
        RolePo rolePo = roleMapper.selectByPrimaryKey(roleid);
        if(userpo==null || privilegepo==null || rolePo==null){
            return new ReturnObject<VoObject>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }

        ReturnObject<Role> retObj = null;
        //获取当前时间
        LocalDateTime localDateTime = LocalDateTime.now();
        RolePrivilege rolePrivilege = new RolePrivilege();

        //查询是否角色已经存在此权限
        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();
        criteria.andPrivilegeIdEqualTo(privid);
        criteria.andRoleIdEqualTo(roleid);
        List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
        RolePrivilegePo roleprivilegepo = new RolePrivilegePo();

        if(rolePrivilegePos.isEmpty()){
            roleprivilegepo.setRoleId(roleid);
            roleprivilegepo.setPrivilegeId(privid);
            roleprivilegepo.setCreatorId(userid);
            roleprivilegepo.setGmtCreate(localDateTime);

            StringBuilder signature = Common.concatString("-", roleprivilegepo.getRoleId().toString(),
                    roleprivilegepo.getPrivilegeId().toString(), roleprivilegepo.getCreatorId().toString(), localDateTime.toString());
            String newSignature = SHA256.getSHA256(signature.toString());
            roleprivilegepo.setSignature(newSignature);

            try {
                int ret = rolePrivilegePoMapper.insert(roleprivilegepo);

                if (ret == 0) {
                    //插入失败
                    return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
                } else {
                    //插入成功
                    //清除角色权限
                    String key = "r_" + roleid;
                    if(redisTemplate.hasKey(key)){
                        redisTemplate.delete(key);
                    }
                }
            }catch (DataAccessException e){
                // 数据库错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
            }catch (Exception e) {
                // 其他Exception错误
                return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了错误：%s", e.getMessage()));
            }

        }else{
//            FIELD_NOTVALID
            return new ReturnObject<>(ResponseCode.FIELD_NOTVALID, String.format("角色权限已存在"));
        }

        //组装返回的bo
        rolePrivilege.setId(roleprivilegepo.getId());
        rolePrivilege.setCreator(userpo);
        rolePrivilege.setRole(rolePo);
        rolePrivilege.setPrivilege(privilegepo);
        rolePrivilege.setGmtModified(localDateTime.toString());

        return new ReturnObject<VoObject>(rolePrivilege);
    }

    /**
     * 由RolePrivilege Id 删除 角色权限
     *
     * @param id: RolePrivilege Id
     * @return void
     * created by 王琛 24320182203277
     */

    public ReturnObject<Object> delPrivByPrivRoleId(Long id){
        ReturnObject<Object> retObj = null;
        RolePrivilegePo rolePrivilegePo = rolePrivilegePoMapper.selectByPrimaryKey(id);
        int ret = rolePrivilegePoMapper.deleteByPrimaryKey(id);
        if(ret==0){
            retObj = new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }else{
            Long roleid = rolePrivilegePo.getRoleId();
            String key = "r_" + roleid;
            //清除缓存被删除的的角色,重新load
            if(redisTemplate.hasKey(key)){
                redisTemplate.delete(key);
            }
            retObj = new ReturnObject<>();
        }

        return retObj;
    }

    /**
     * 由Role Id 获取 角色权限
     *
     * @param id: Role Id
     * @return List<RolePrivilegeRetVo>
     * created by 王琛 24320182203277
     */

    public ReturnObject<List> getRolePrivByRoleId(Long id){
        String key = "r_" + id;
        List<RolePrivilege> rolepribilegere = new ArrayList<>();
        RolePrivilegePoExample example = new RolePrivilegePoExample();
        RolePrivilegePoExample.Criteria criteria = example.createCriteria();



        //查看是否有此角色
        RolePo rolePo = roleMapper.selectByPrimaryKey(id);
        if(rolePo==null){
            return new ReturnObject<>(ResponseCode.RESOURCE_ID_NOTEXIST);
        }
        RolePrivilege e = new RolePrivilege();

        List<Long> privids = getPrivIdsByRoleId(id);

        for(Long pid: privids){
            example.clear();
            criteria.andRoleIdEqualTo(id);
            criteria.andPrivilegeIdEqualTo(pid);
            List<RolePrivilegePo> rolePrivilegePos = rolePrivilegePoMapper.selectByExample(example);
            if(rolePrivilegePos!=null && rolePrivilegePos.size()>0 && rolePrivilegePos.get(0)!=null){

                UserPo userpo = userMapper.selectByPrimaryKey(rolePrivilegePos.get(0).getCreatorId());
                PrivilegePo privilegepo = privilegePoMapper.selectByPrimaryKey(pid);

                //组装权限bo
                e.setCreator(userpo);
                e.setId(pid);
                e.setPrivilege(privilegepo);
                e.setRole(rolePo);
                e.setGmtModified(rolePrivilegePos.get(0).getGmtCreate().toString());

                rolepribilegere.add(e);
            }
        }

        return new ReturnObject<>(rolepribilegere);
    }

}
