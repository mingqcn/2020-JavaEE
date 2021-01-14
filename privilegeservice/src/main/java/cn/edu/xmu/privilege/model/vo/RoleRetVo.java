package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色返回视图
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Data
@ApiModel(description = "角色视图对象")
public class RoleRetVo {
    @ApiModelProperty(value = "角色id")
    private Long id;

    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色描述")
    private String desc;

    @ApiModelProperty(value = "创建者")
    private Long createdBy;

    @ApiModelProperty(value = "部门id")
    private Long departId;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime gmtCreate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime gmtModified;

    /**
     * 用Role对象建立Vo对象
     *
     * @author 24320182203281 王纬策
     * @param role role
     * @return RoleRetVo
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public RoleRetVo(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.desc = role.getDescribe();
        this.createdBy = role.getCreatorId();
        this.departId = role.getDepartId();
        this.gmtCreate = role.getGmtCreate();
        this.gmtModified = role.getGmtModified();
    }
}
