package cn.edu.xmu.privilege.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 权限传值对象
 * @author Ming Qiu
 * @date Created in 2020/11/4 0:08
 **/
@Data
@ApiModel("权限传值对象")
public class PrivilegeVo {
    @NotNull(message = "name不得为空")
    private String name;
    @NotNull(message = "url不得为空")
    private String url;

    @NotNull(message = "requestType不得为空")
    @Range(min = 0, max = 3, message = "错误的requestType数值")
    private Byte requestType;
}
