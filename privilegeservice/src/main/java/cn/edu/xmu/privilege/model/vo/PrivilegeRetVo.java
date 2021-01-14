package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Privilege;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限返回VO
 * @author Ming Qiu
 * @date Created in 2020/11/3 23:34
 **/
@Data
public class PrivilegeRetVo {

    private Long id;

    private String name;

    private String url;

    private Integer requestType;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    public PrivilegeRetVo(Privilege obj){
        this.id = obj.getId();
        this.name = obj.getName();
        this.url = obj.getUrl();
        this.requestType = obj.getRequestType().getCode();
        this.gmtCreate = obj.getGmtCreate();
        this.gmtModified = obj.getGmtModified();
    }
}
