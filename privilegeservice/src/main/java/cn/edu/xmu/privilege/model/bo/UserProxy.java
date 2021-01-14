package cn.edu.xmu.privilege.model.bo;

import cn.edu.xmu.privilege.model.vo.UserProxyVo;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.encript.SHA256;
import cn.edu.xmu.privilege.model.po.UserProxyPo;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用户代理Bo类
 *
 * @author 24320182203221 李狄翰
 * createdBy 李狄翰2020/11/09 12:00
 **/
@Data
public class UserProxy{
    private Long id;
    private Long a_id;
    private Long b_id;
    private LocalDateTime begin_time;
    private LocalDateTime end_time;
    private LocalDateTime gmtCreate;
    private String signature;
    private String cacuSignature;

    public UserProxy() {
    }

    public UserProxy(UserProxyVo vo) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.begin_time = LocalDateTime.parse(vo.getBeginDate(), df);
        this.end_time = LocalDateTime.parse(vo.getEndDate(), df);

    }

    /**
     * 构造函数
     *

     */
    public UserProxy(UserProxyPo po) {
        this.id = po.getId();
        this.a_id = po.getUserAId();
        this.b_id = po.getUserBId();
        this.begin_time = po.getBeginDate();
        this.end_time = po.getEndDate();
        this.gmtCreate = po.getGmtCreate();
        this.signature = po.getSignature();
        StringBuilder signature = Common.concatString("-", po.getUserAId().toString(), po.getUserBId().toString(),po.getBeginDate().toString(),po.getEndDate().toString(),po.getValid().toString());
        this.cacuSignature = SHA256.getSHA256(signature.toString());
    }

    public Boolean authetic() {
        return this.cacuSignature.equals(this.signature);
    }
}
