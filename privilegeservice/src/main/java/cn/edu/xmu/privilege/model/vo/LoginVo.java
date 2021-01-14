package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * @auther mingqiu
 * @date 2020/6/27 下午7:54
 */
@ApiModel
@Data
public class LoginVo {
    @NotBlank(message = "必须输入用户名")
    @ApiModelProperty(name = "用户名", value = "testuser")
    private String userName;

    @NotBlank(message = "必须输入密码")
    @ApiModelProperty(name = "密码", value = "123456r")
    private String password;
}
