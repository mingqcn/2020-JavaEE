package cn.edu.xmu.privilege.dao;

import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.privilege.mapper.UserPoMapper;
import cn.edu.xmu.privilege.mapper.UserProxyPoMapper;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.po.UserProxyPo;
import cn.edu.xmu.privilege.model.po.UserProxyPoExample;
import cn.edu.xmu.privilege.model.vo.UserProxyVo;
import cn.edu.xmu.privilege.model.bo.UserProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * @author Di Han Li
 * @date Created in 2020/11/4 9:08
 * Modified by 24320182203221 李狄翰 at 2020/11/8 8:00
 **/
@Repository
public class UserProxyDao {

    private static final Logger logger = LoggerFactory.getLogger(UserProxyDao.class);

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserProxyPoMapper userProxyPoMapper;
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private UserPoMapper userPoMapper;

    public ReturnObject usersProxy(Long aid, Long id, UserProxy bo,Long departid) {
        if(Objects.equals(aid,id)){
            return new ReturnObject(ResponseCode.USERPROXY_SELF);
        }
        if(!isBiggerBegin(bo)){
            return new ReturnObject(ResponseCode.USERPROXY_BIGGER);
        }
        if (isExistProxy(aid, id, bo)) {
            return new ReturnObject(ResponseCode.USERPROXY_CONFLICT);
        }
        if(userPoMapper.selectByPrimaryKey(id).getDepartId()!=departid)
        {
            return new ReturnObject(ResponseCode.USERPROXY_DEPART_CONFLICT);
        }
        UserProxyPo userProxyPo = new UserProxyPo();
        userProxyPo.setUserAId(aid);
        userProxyPo.setUserBId(id);
        userProxyPo.setDepartId(departid);
        userProxyPo.setValid((byte) 0);
        userProxyPo.setBeginDate(bo.getBegin_time());
        userProxyPo.setEndDate(bo.getEnd_time());
        userProxyPo.setGmtCreate(LocalDateTime.now());
        StringBuilder signature = Common.concatString("-", userProxyPo.getUserAId().toString(), userProxyPo.getUserBId().toString(),userProxyPo.getBeginDate().toString(),userProxyPo.getEndDate().toString(),userProxyPo.getValid().toString());
        userProxyPo.setSignature(SHA256.getSHA256(signature.toString()));
        try {
            userProxyPoMapper.insert(userProxyPo);
            return new ReturnObject();
        }
        catch (DataAccessException e) {
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
    }

    public ReturnObject aUsersProxy(Long aid, Long bid, UserProxy bo,Long departid) {
        if(Objects.equals(aid,bid)){
            return new ReturnObject(ResponseCode.USERPROXY_SELF);
        }

        if(!isBiggerBegin(bo)){
            return new ReturnObject(ResponseCode.USERPROXY_BIGGER);
        }

        if (isExistProxy(aid, bid, bo)) {
            return new ReturnObject(ResponseCode.USERPROXY_CONFLICT);
        }

        if(userPoMapper.selectByPrimaryKey(aid).getDepartId()!=userPoMapper.selectByPrimaryKey(bid).getDepartId())
        {
            return new ReturnObject(ResponseCode.USERPROXY_DEPART_CONFLICT);
        }
        if(!Objects.equals(departid, 0L) && departid != userPoMapper.selectByPrimaryKey(aid).getDepartId())
        {
            return new ReturnObject(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT);
        }

        UserProxyPo userProxyPo = new UserProxyPo();
        userProxyPo.setUserAId(aid);
        userProxyPo.setUserBId(bid);
        userProxyPo.setDepartId(userPoMapper.selectByPrimaryKey(aid).getDepartId());
        userProxyPo.setValid((byte) 0);
        userProxyPo.setBeginDate(bo.getBegin_time());
        userProxyPo.setEndDate(bo.getEnd_time());
        userProxyPo.setGmtCreate(LocalDateTime.now());
        StringBuilder signature = Common.concatString("-", userProxyPo.getUserAId().toString(), userProxyPo.getUserBId().toString(),userProxyPo.getBeginDate().toString(),userProxyPo.getEndDate().toString(),userProxyPo.getValid().toString());
        userProxyPo.setSignature(SHA256.getSHA256(signature.toString()));
        try {
            userProxyPoMapper.insert(userProxyPo);
            return new ReturnObject();
        }
        catch (DataAccessException e) {
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
    }

    public ReturnObject removeUserProxy(Long id, Long aid) {
        UserProxyPo userProxyPo = userProxyPoMapper.selectByPrimaryKey(id);
        if (aid.compareTo(userProxyPo.getUserAId()) == 0) {
            try {
                userProxyPoMapper.deleteByPrimaryKey(id);
                return new ReturnObject();
            }
            catch (DataAccessException e) {
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
        } else {
            return new ReturnObject(ResponseCode.RESOURCE_ID_OUTSCOPE);
        }
    }

    public ReturnObject listProxies(Long aId, Long bId,Long did) {
        UserProxyPoExample example = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        if (aId != null) {
            criteria.andUserAIdEqualTo(aId);
        }
        if (bId != null) {
            criteria.andUserBIdEqualTo(bId);
        }
        if(!Objects.equals(did, 0L))
        {
            criteria.andDepartIdEqualTo(did);
        }
        List<UserProxyPo> results = userProxyPoMapper.selectByExample(example);
        for(UserProxyPo po : results){
            UserProxy bo=new UserProxy(po);
            if (!bo.authetic()) {
                StringBuilder message = new StringBuilder().append("listProxies: ").append(ResponseCode.RESOURCE_FALSIFY.getMessage()).append(" id = ")
                        .append(bo.getId());
                logger.error(message.toString());
                return new ReturnObject<>(ResponseCode.RESOURCE_FALSIFY);
            }
        }
        return new ReturnObject<>(results);
    }

    public ReturnObject removeAllProxies(Long id,Long did) {
        if(!Objects.equals(did, 0L) && did!=userProxyPoMapper.selectByPrimaryKey(id).getDepartId())
        {
            return new ReturnObject(ResponseCode.USERPROXY_DEPART_MANAGER_CONFLICT);
        }
        try {
            userProxyPoMapper.deleteByPrimaryKey(id);
            return new ReturnObject();
        }
        catch (DataAccessException e) {
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
    }

    public boolean isBiggerBegin(UserProxy bo){
        LocalDateTime nowBeginDate = bo.getBegin_time();
        LocalDateTime nowEndDate = bo.getEnd_time();
        return nowEndDate.isAfter(nowBeginDate);
    }

    /**
     * 判断同一时间段是否有冲突的代理关系
     *
     * @param aId
     * @param bId
     * @param bo
     * @return
     */
    public boolean isExistProxy(Long aId, Long bId, UserProxy bo) {
        boolean isExist = false;
        UserProxyPoExample example = new UserProxyPoExample();
        UserProxyPoExample.Criteria criteria = example.createCriteria();
        criteria.andUserAIdEqualTo(aId);
        criteria.andUserBIdEqualTo(bId);
        List<UserProxyPo> results = userProxyPoMapper.selectByExample(example);
        if (results != null && results.size() > 0) {
            LocalDateTime nowBeginDate = bo.getBegin_time();
            LocalDateTime nowEndDate = bo.getEnd_time();
            for (UserProxyPo po: results){
                LocalDateTime beginDate = po.getBeginDate();
                LocalDateTime endDate = po.getEndDate();

                //判断开始时间和失效时间是不是不在同一个区间里面
                if(nowBeginDate.equals(beginDate) || nowBeginDate.equals(endDate) || (nowBeginDate.isAfter(beginDate) && nowBeginDate.isBefore(endDate)) ){
                    isExist = true;
                    break;
                }
                if(nowEndDate.equals(beginDate) || nowEndDate.equals(endDate) || (nowEndDate.isAfter(beginDate) && nowEndDate.isBefore(endDate)) ){
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }
}
