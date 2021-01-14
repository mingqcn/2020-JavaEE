package cn.edu.xmu.timer.model.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 17:29
 **/
@Data
public class TaskMessage {
    private LocalDateTime beginTime;

    private String beanName;

    private String methodName;

    private String returnTypeName;

    /**
     * 按照seq排序
     */
    private List<Param> paramList;
}
