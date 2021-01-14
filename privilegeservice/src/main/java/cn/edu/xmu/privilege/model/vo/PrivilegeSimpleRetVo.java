package cn.edu.xmu.privilege.model.vo;

import cn.edu.xmu.privilege.model.bo.Privilege;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限返回简单VO
 * @author Ming Qiu
 * @date Created in 2020/11/7 16:34
 **/
@Data
public class PrivilegeSimpleRetVo {

    private Long id;

    private String name;

    public PrivilegeSimpleRetVo(Privilege obj){
        this.id = obj.getId();
        this.name = obj.getName();
    }
}
