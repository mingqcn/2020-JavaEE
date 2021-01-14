package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "重置密码对象")
public class ResetPwdVo {
    private String mobile;
    private String email;
}

