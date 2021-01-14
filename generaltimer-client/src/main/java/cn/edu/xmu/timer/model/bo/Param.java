package cn.edu.xmu.timer.model.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 17:27
 **/
@Data
public class Param{

    private Long id;

    private Integer seq;

    private String typeName;

    private String paramValue;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
