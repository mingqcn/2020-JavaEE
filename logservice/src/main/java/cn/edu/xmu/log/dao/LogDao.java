package cn.edu.xmu.log.dao;

import cn.edu.xmu.log.mapper.LogPoMapper;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.log.model.po.LogPoExample;
import cn.edu.xmu.ooad.model.VoObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 日志服务类
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Slf4j
@Repository
public class LogDao {
    @Autowired
    private LogPoMapper logPoMapper;

    /**
     * 查询日志
     *
     * @param logInfo log条件
     * @param pageNum 页码
     * @param pageSize 页面大小
     *
     * @author 王纬策
     * @date Created in 2020/11/18 10:33
     * @date Modified in 2020/11/18 19:32
     **/
    private static final Logger logger = LoggerFactory.getLogger(LogDao.class);

    public ReturnObject<PageInfo<VoObject>> selectLogs(Log logInfo, Integer pageNum, Integer pageSize) {
        LogPoExample example = new LogPoExample();
        LogPoExample.Criteria criteria = example.createCriteria();
        if(logInfo.getDepartId() != null){
            log.debug("selectLogs departId="+logInfo.getDepartId());
            if(logInfo.getDepartId() != 0L) {
                criteria.andDepartIdEqualTo(logInfo.getDepartId());
            }
        }
        if(logInfo.getUserId() != null){
            log.debug("selectLogs userId="+logInfo.getUserId());
            criteria.andUserIdEqualTo(logInfo.getUserId());
        }
        if(logInfo.getIp() != null){
            log.debug("selectLogs ip="+logInfo.getIp());
            criteria.andIpEqualTo(logInfo.getIp());
        }
        if(logInfo.getPrivilegeId() != null){
            log.debug("selectLogs privilegeId="+logInfo.getPrivilegeId());
            criteria.andPrivilegeIdEqualTo(logInfo.getPrivilegeId());
        }
        if(logInfo.getSuccess() != null){
            log.debug("selectLogs success="+logInfo.getSuccess());
            criteria.andSuccessEqualTo(logInfo.getSuccess());
        }
        if(logInfo.getBeginDate() != null){
            log.debug("selectLogs beginDate="+logInfo.getBeginDate());
            criteria.andGmtCreateGreaterThan(Timestamp.valueOf(logInfo.getBeginDate()).toLocalDateTime());
        }
        if(logInfo.getEndDate() != null){
            log.debug("selectLogs endDate="+logInfo.getEndDate());
            criteria.andGmtCreateLessThan(Timestamp.valueOf(logInfo.getEndDate()).toLocalDateTime());
        }

        try {
            PageHelper.startPage(pageNum, pageSize);
            List<LogPo> logPos = logPoMapper.selectByExample(example);
            log.debug("selectLogs DAO:"+logPos);

            PageInfo<LogPo> logPagePos = new PageInfo<>(logPos);
            List<VoObject> operationLogs = logPagePos.getList().stream().map(Log::new).collect(Collectors.toList());

            PageInfo<VoObject> returnObject = new PageInfo<>(operationLogs);
            returnObject.setPages(logPagePos.getPages());
            returnObject.setPageNum(logPagePos.getPageNum());
            returnObject.setPageSize(logPagePos.getPageSize());
            returnObject.setTotal(logPagePos.getTotal());

            return new ReturnObject<>(returnObject);
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
     * @description 插入日志
     * @param log 日志
     * @return void
     * @author Xianwei Wang
     * created at 11/18/20 11:40 AM
     */
    public void insertLog(Log log) {
        try {
            logPoMapper.insert(log.createPo());
        } catch (Exception e) {
            logger.error("严重错误：" + e.getMessage());
        }
    }

    /**
     * 清空日志
     *
     * @param departId 部门ID
     * @return ReturnObject<Object> 删除结果
     * createdBy 李狄翰 2020/11/18 10:57
     * @author 24320182203221 李狄翰
     */
    public ReturnObject<Object> deleteLogs(Log log, Long departId) {
        logger.debug("deleteLogs");
        LogPoExample example = new LogPoExample();
        LogPoExample.Criteria criteria = example.createCriteria();
        criteria.andGmtCreateBetween(log.getBeginTime(), log.getEndTime());
        //判断是否为管理员
        if (!Objects.equals(departId, 0L)) {
            criteria.andDepartIdEqualTo(departId);
        }
        try {
            int total = logPoMapper.deleteByExample(example);
            ReturnObject<Object> retObj = new ReturnObject<>(total);
            return retObj;
        }
        catch (DataAccessException e){
            logger.error("clearLog: DataAccessException:" + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("数据库错误：%s", e.getMessage()));
        }
        catch (Exception e) {
            // 其他Exception错误
            logger.error("other exception : " + e.getMessage());
            return new ReturnObject<>(ResponseCode.INTERNAL_SERVER_ERR, String.format("发生了严重的数据库错误：%s", e.getMessage()));
        }
    }




}
