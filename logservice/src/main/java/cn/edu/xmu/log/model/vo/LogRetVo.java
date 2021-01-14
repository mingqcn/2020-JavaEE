package cn.edu.xmu.log.model.vo;

import cn.edu.xmu.log.model.bo.Log;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 日志返回Vo
 * @Author 王纬策
 *
 */
@Data
@ApiModel(description = "日志返回对象")
public class LogRetVo {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "登录的ip")
    private String ip;

    @ApiModelProperty(value = "描述")
    private String desc;

    @ApiModelProperty(value = "部门ID")
    private Long departId;

    @ApiModelProperty(value = "权限ID")
    private Long privilegeId;

    @ApiModelProperty(value = "是否成功")
    private Byte success;

    @ApiModelProperty(value = "时间")
    private String operationDate;

    @Override
    public String toString() {
        return "LogRetVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", ip='" + ip + '\'' +
                ", desc='" + desc + '\'' +
                ", departId=" + departId +
                ", privilegeId=" + privilegeId +
                ", success=" + success +
                ", operationDate='" + operationDate + '\'' +
                '}';
    }

    public LogRetVo(Log bo) {
        this.setId(bo.getId());
        this.setDepartId(bo.getDepartId());
        this.setUserId(bo.getUserId());
        this.setIp(bo.getIp());
        this.setDesc(bo.getDesc());
        this.setPrivilegeId(bo.getPrivilegeId());
        this.setSuccess((bo.getSuccess()));
        this.setOperationDate(bo.getGmtCreate().toString());
    }

}
