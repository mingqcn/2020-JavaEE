package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限传值对象
 *
 * @author Di Han Li
 * @date Created in 2020/11/4 9:08
 * Modified by 24320182203221 李狄翰 at 2020/11/8 8:00
 **/
@Data
@ApiModel("用户代理传值对象")
public class UserProxyVo {

    @ApiModelProperty(name = "代理开始时间", value = "beginDate", required = true)
    private String beginDate;

    @ApiModelProperty(name = "代理过期时间", value = "beginDate", required = true)
    private String endDate;


}