package cn.edu.xmu.log.model.bo;

import cn.edu.xmu.log.model.po.LogPo;
import cn.edu.xmu.log.model.vo.LogRetVo;
import cn.edu.xmu.log.model.vo.LogVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日志Bo
 * @Author 王纬策
 *
 */
@Data
public class Log implements VoObject {
    /**
     *
     * id 主键
     * user_id 用户ID
     * ip 登录的ip
     * desc 描述
     * privilege_id 权限ID
     */
    private Long id;
    private Long userId;
    private Long departId;
    private String ip;
    private String desc;
    private Long privilegeId;
    private LocalDateTime gmtCreate;
    private Byte success;
    private String beginDate;
    private String endDate;

    private LocalDateTime beginTime;
    private LocalDateTime endTime;

    /**
     * 构造函数
     * @param po Po对象
     */
    public Log(LogPo po){
        this.setId(po.getId());
        this.setUserId(po.getUserId());
        this.setDepartId(po.getDepartId());
        this.setIp(po.getIp());
        this.setDesc(po.getDescr());
        this.setPrivilegeId(po.getPrivilegeId());
        this.setGmtCreate(po.getGmtCreate());
        this.setSuccess(po.getSuccess());
    }
    public Log() {

    }
    public Log(LogVo vo) {
        this.setDepartId(vo.getDepartId());
        this.setUserId(vo.getUserId());
        this.setIp(vo.getIp());
        this.setPrivilegeId(vo.getPrivilegeId());
        this.setSuccess(vo.getSuccess());
        this.setBeginDate(vo.getBeginDate());
        this.setEndDate(vo.getEndDate());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if(StringUtils.isNotBlank(vo.getBeginTime())){
            this.beginTime = LocalDateTime.parse(vo.getBeginTime(), df);
        }
        if(StringUtils.isNotBlank(vo.getEndTime())){
            this.endTime = LocalDateTime.parse(vo.getEndTime(), df);
        }
    }

    /**
     * Create return Vo object
     * @author 王纬策
     * @return
     */
    @Override
    public LogRetVo createVo() {
        LogRetVo logRetVo = new LogRetVo(this);
        return logRetVo;
    }

    /**
     * 创建SimpleVo
     * @return userSimpleRetVo
     * @author 王纬策
     */
    @Override
    public LogRetVo createSimpleVo() {
        LogRetVo logRetVo = new LogRetVo(this);
        return logRetVo;
    }

    /**
     * @description 生成po
     * @return cn.edu.xmu.log.model.po.LogPo
     * @author Xianwei Wang
     * created at 11/18/20 2:59 PM
     */
    public LogPo createPo() {
        LogPo logPo = new LogPo();

        logPo.setUserId(this.userId);
        logPo.setIp(this.ip);
        logPo.setDescr(this.desc);
        logPo.setGmtCreate(this.gmtCreate);
        logPo.setPrivilegeId(this.privilegeId);
        logPo.setSuccess(this.success);
        logPo.setDepartId(this.departId);

        return logPo;
    }

}
