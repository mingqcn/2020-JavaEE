package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.RolePrivilege;
import lombok.Data;

/**
 * 权限角色返回值对象
 * @author wc 24320182203277
 * @date
 **/

@Data
public class RolePrivilegeRetVo {
    private Long id;
    private simple_role role = new simple_role();
    private simple_privilege privilege = new simple_privilege();
    private simple_creator creator = new simple_creator();
    private String gmtModified;

    public RolePrivilegeRetVo(RolePrivilege obj){
        this.id = obj.getId();
        this.role.set(obj.getRole().getId(), obj.getRole().getName());
        this.privilege.set(obj.getPrivilege().getId(), obj.getPrivilege().getName());
        this.creator.set(obj.getCreator().getId(), obj.getCreator().getUserName());
        this.gmtModified = obj.getGmtModified();
    }
}

@Data
class simple_role{
    public Long id;
    public String name;
    public void set(Long id, String name){
        this.id = id;
        this.name = name;
    }
}

@Data
class simple_privilege{
    public Long id;
    public String name;
    public void set(Long id, String name){
        this.id = id;
        this.name = name;
    }
}

@Data
class simple_creator{
    public Long id;
    public String username;
    public void set(Long id, String username){
        this.id = id;
        this.username = username;
    }
}
