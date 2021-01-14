package cn.edu.xmu.oomall;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @auther mingqiu
 * @date 2020/6/27 下午7:54
 */
@Data
public class LoginVo {
    @NotBlank(message = "必须输入用户名")
    private String userName;

    @NotBlank(message = "必须输入密码")
    private String password;
}
