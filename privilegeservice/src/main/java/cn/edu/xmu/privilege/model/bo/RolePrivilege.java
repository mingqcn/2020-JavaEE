package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.privilege.model.po.PrivilegePo;
import cn.edu.xmu.privilege.model.po.RolePo;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.vo.RolePrivilegeRetVo;
import cn.edu.xmu.privilege.model.vo.RoleSimpleRetVo;
import lombok.Data;

/**
 * 角色权限
 * @author wc 24320182203277
 * @date
 **/

@Data
public class RolePrivilege implements VoObject {
    private Long id= null;
    private RolePo role = new RolePo();
    private PrivilegePo privilege = new PrivilegePo();
    private UserPo creator = new UserPo();
    private String gmtModified = null;
    @Override
    public RolePrivilegeRetVo createVo() {
        return new RolePrivilegeRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return new RolePrivilegeRetVo(this);
    }
}
