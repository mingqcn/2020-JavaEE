package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.po.UserPo;
import lombok.Data;

@Data
public class UserRetVo {
    private Long id;
    private String userName;
    private String mobile;
    private String name;
    private String email;
    private String avatar;
    private String lastLoginTime;
    private String lastLoginIp;
    private Byte status;
    private Long depart_id;
    private String gmtCreate;
    private String gmtModified;
}
