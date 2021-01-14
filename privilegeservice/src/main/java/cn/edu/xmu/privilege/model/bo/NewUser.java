package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.privilege.model.po.NewUserPo;
import cn.edu.xmu.privilege.model.vo.NewUserVo;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新用户Bo
 * @Author LiangJi@3229
 *
 */
@Data
public class NewUser implements VoObject {

    private String email;
    private String mobile;
    private String userName;
    private String password;
    private String avatar;
    private String name;
    private Long departId;
    private String openId;
    private LocalDateTime gmtCreated;
    public NewUser(NewUserVo vo){
        this.email=AES.encrypt(vo.getEmail(),User.AESPASS);
        this.mobile=AES.encrypt(vo.getMobile(),User.AESPASS);
        this.userName=vo.getUserName();
        this.password=AES.encrypt(vo.getPassword(),User.AESPASS);
        this.avatar=vo.getAvatar();
        this.name=AES.decrypt(vo.getName(),User.AESPASS);
        this.departId=vo.getDepartId();
        this.openId=vo.getOpenId();
        this.gmtCreated=LocalDateTime.now();
        this.setEmail(AES.encrypt(vo.getEmail(), User.AESPASS));
    }
    @Override
    public Object createVo() {
        return null;
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }
}
