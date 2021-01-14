package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.privilege.model.po.UserRolePo;
import cn.edu.xmu.privilege.model.vo.RoleRetVo;
import cn.edu.xmu.privilege.model.vo.UserSimpleRetVo;
import cn.edu.xmu.privilege.model.vo.UserRoleRetVo;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRole implements VoObject {
    private Long id;

    private User user;

    private Role role;

    private User creator;

    private LocalDateTime gmtCreate;

    private String signature;

    private String cacuSignature;

    public UserRole(UserRolePo userRolePo, User user, Role role, User creator){
        this.id = userRolePo.getId();
        this.user = user;
        this.role = role;
        this.creator = creator;
        this.gmtCreate = userRolePo.getGmtCreate();
        this.signature = userRolePo.getSignature();

        StringBuilder signature = Common.concatString("-",
                userRolePo.getUserId().toString(), userRolePo.getRoleId().toString(), userRolePo.getCreatorId().toString());
        this.cacuSignature = SHA256.getSHA256(signature.toString());
    }

    /**
     * 对象未篡改
     * @return
     */
    public Boolean authetic() {
        return this.cacuSignature.equals(this.signature);
    }

    @Override
    public Object createVo() {
        UserRoleRetVo userRoleRetVo = new UserRoleRetVo();
        userRoleRetVo.setId(this.id);
        userRoleRetVo.setUser(this.user.createSimpleVo());
        userRoleRetVo.setCreator(this.creator.createSimpleVo());
        userRoleRetVo.setRole(this.role.createSimpleVo());
        userRoleRetVo.setGmtCreate(this.gmtCreate.toString());

        return userRoleRetVo;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
