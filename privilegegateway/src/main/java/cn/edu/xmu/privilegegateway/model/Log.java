package cn.edu.xmu.privilegegateway.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Log {
    private Long id;
    private Long userId;
    private Long departId;
    private String ip;
    private String desc;
    private Long privilegeId;
    private LocalDateTime gmtCreate;
    private Byte success;
    private String beginDate;
    private String endDate;
    private String uuid;

    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}
