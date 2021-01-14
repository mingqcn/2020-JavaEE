package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 角色视图
 *
 * @author 24320182203281 王纬策
 * createdBy 王纬策 2020/11/04 13:57
 * modifiedBy 王纬策 2020/11/7 19:20
 **/
@Data
@ApiModel(description = "角色视图对象")
public class RoleVo {
    @NotBlank(message = "角色名不能为空")
    @ApiModelProperty(value = "角色名称")
    private String name;

    @ApiModelProperty(value = "角色描述")
    private String descr;

    /**
     * 构造函数
     *
     * @author 24320182203281 王纬策
     * @return Role
     * createdBy 王纬策 2020/11/04 13:57
     * modifiedBy 王纬策 2020/11/7 19:20
     */
    public Role createRole() {
        Role role = new Role();
        role.setDescribe(this.descr);
        role.setName(this.name);
        return role;
    }
}
