package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

/**
 * 用户信息 Vo
 * @author 19720182203919 李涵
 * Created at 2020/11/4 20:30
 * Modified by 19720182203919 李涵 at 2020/11/5 10:42
 **/
@Data
@ApiModel(description = "管理员用户信息视图对象")
public class UserVo {

    @ApiModelProperty(value = "用户姓名")
    private String name;

    @ApiModelProperty(value = "用户头像 URL")
    private String avatar;

    @Pattern(regexp = "[+]?[0-9*#]+",
            message = "手机号码格式不正确")
    @ApiModelProperty(value = "用户手机号")
    private String mobile;

    @Pattern(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
            message = "Email 格式不正确")
    @ApiModelProperty(value = "用户 Email 地址")
    private String email;

}
