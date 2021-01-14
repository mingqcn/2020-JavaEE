package cn.edu.xmu.timer.model.bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Ming Qiu
 * @date Created in 2020/12/1 17:24
 **/
@Data
public class Task {

    private Long id;

    private LocalDateTime beginTime;

    private String beanName;

    private String senderName;

    private String methodName;

    private String returnTypeName;

    private String topic;

    private String tag;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    private Byte period;

    /**
     * 按照seq排序
     */
    private List<Param> paramList;

    /**
    * 由Task创建TaskMessage
    * @Author: Zeyao Feng
    * @Date: Created in 2020-12-02 12:12
    */
    public TaskMessage createTaskMessage(){
        TaskMessage message=new TaskMessage();
        message.setBeginTime(this.beginTime);
        message.setBeanName(this.getBeanName());
        message.setMethodName(this.methodName);
        message.setReturnTypeName(this.returnTypeName);
        message.setParamList(this.getParamList());
        return message;
    }

}
