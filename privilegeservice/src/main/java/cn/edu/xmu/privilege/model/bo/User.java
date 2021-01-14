package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.AES;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.privilege.model.po.UserPo;
import cn.edu.xmu.privilege.model.vo.UserRetVo;
import cn.edu.xmu.privilege.model.vo.UserSimpleRetVo;
import cn.edu.xmu.privilege.model.vo.UserVo;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台用户
 *
 * @author Ming Qiu
 * @date Created in 2020/11/3 20:10
 * Modified at 2020/11/4 21:23
 **/
@Data
public class User implements VoObject {

    public static String AESPASS = "OOAD2020-11-01";

    /**
     * 后台用户状态
     */
    public enum State {
        NEW(0, "新注册"),
        NORM(1, "正常"),
        FORBID(2, "封禁"),
        DELETE(3, "废弃");

        private static final Map<Integer, User.State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (User.State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static User.State getTypeByCode(Integer code) {
            return stateMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    private Long id;

    private String userName;

    private String password;

    private String mobile;

    private Boolean mobileVerified = false;

    private String email;

    private Boolean emailVerified = false;

    private String name;

    private String avatar;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private String openId;

    private State state = State.NEW;

    private Long departId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Long creatorId;

    private String signature;

    private String cacuSignature;

    /**
     * 构造函数
     * @param po Po对象
     */
    public User(UserPo po){
        this.id = po.getId();
        this.userName = po.getUserName();
        this.password =po.getPassword();
        this.mobile = AES.decrypt(po.getMobile(),AESPASS);
        if (null != po.getMobileVerified()) {
            this.mobileVerified = po.getMobileVerified() == 1;
        }
        this.email = AES.decrypt(po.getEmail(),AESPASS);

        if (null != po.getEmailVerified()) {
            this.emailVerified = po.getEmailVerified() == 1;
        }
        this.name = AES.decrypt(po.getName(), AESPASS);
        this.avatar = po.getAvatar();
        this.lastLoginTime = po.getLastLoginTime();
        this.lastLoginIp = po.getLastLoginIp();
        this.openId = po.getOpenId();
        if (null != po.getState()) {
            this.state = State.getTypeByCode(po.getState().intValue());
        }
        this.departId = po.getDepartId();
        this.creatorId = po.getCreatorId();
        this.gmtCreate = po.getGmtCreate();
        this.gmtModified = po.getGmtModified();
        this.signature = po.getSignature();

        StringBuilder signature = Common.concatString("-", po.getUserName(), po.getPassword(),
                po.getMobile(),po.getEmail(),po.getOpenId(),po.getState().toString(),po.getDepartId().toString(),
                po.getCreatorId().toString());
        this.cacuSignature = SHA256.getSHA256(signature.toString());
    }


    /**
     * Create return Vo object
     * @author XQChen
     * @return
     */
    @Override
    public UserRetVo createVo() {
        UserRetVo userRetVo = new UserRetVo();
        userRetVo.setId(id);
        userRetVo.setUserName(userName);
        userRetVo.setMobile(mobile);
        userRetVo.setName(name);
        userRetVo.setEmail(email);
        userRetVo.setAvatar(avatar);
        userRetVo.setLastLoginTime(lastLoginTime.toString());
        userRetVo.setLastLoginIp(lastLoginIp);
        userRetVo.setStatus(state.getCode().byteValue());
        userRetVo.setDepart_id(departId);
        userRetVo.setGmtCreate(gmtCreate.toString());
        userRetVo.setGmtModified(gmtModified.toString());

        return userRetVo;
    }

    /**
     * 对象未篡改
     * @return
     */
    public Boolean authetic() {
        return this.cacuSignature.equals(this.signature);
    }

    /**
     * 用 UserEditVo 对象创建 用来更新 User 的 Po 对象
     * @param vo vo 对象
     * @return po 对象
     */
    public UserPo createUpdatePo(UserVo vo) {
        String nameEnc = vo.getName() == null ? null : AES.encrypt(vo.getName(), User.AESPASS);
        String mobEnc = vo.getMobile() == null ? null : AES.encrypt(vo.getMobile(), User.AESPASS);
        String emlEnc = vo.getEmail() == null ? null : AES.encrypt(vo.getEmail(), User.AESPASS);
        Byte state = (byte) this.state.code;

        UserPo po = new UserPo();
        po.setId(id);
        po.setName(nameEnc);
        po.setAvatar(vo.getAvatar());
        po.setMobile(mobEnc);
        po.setEmail(emlEnc);
        po.setState(state);

        po.setGmtCreate(null);
        po.setGmtModified(LocalDateTime.now());

        // 签名：user_name,password,mobile,email,open_id,state,depart_id,creator
        StringBuilder signature = Common.concatString("-",
                this.getUserName(),
                this.getPassword(),
                mobEnc == null ? AES.encrypt(this.mobile, User.AESPASS) : mobEnc,
                emlEnc == null ? AES.encrypt(this.email, User.AESPASS) : emlEnc,
                this.getOpenId(),
                state.toString(),
                this.getDepartId().toString(),
                this.getCreatorId().toString());
        po.setSignature(SHA256.getSHA256(signature.toString()));
        return po;
    }

    /**
     * 创建SimpleVo
     * @return userSimpleRetVo
     * @author Xianwei Wang
     */
    @Override
    public UserSimpleRetVo createSimpleVo() {
        UserSimpleRetVo userSimpleRetVo = new UserSimpleRetVo();
        userSimpleRetVo.setId(this.id);
        userSimpleRetVo.setUserName(this.userName);

        return userSimpleRetVo;
    }
}
