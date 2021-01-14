package cn.edu.xmu.log.service;

import cn.edu.xmu.log.LogServiceApplication;
import cn.edu.xmu.log.dao.LogDao;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.log.service.mq.LogConsumerListener;
import cn.edu.xmu.ooad.util.JacksonUtil;
import io.lettuce.core.StrAlgoArgs;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author Xianwen Wang
 * created at 11/18/20 10:10 AM
 * @detail cn.edu.xmu.log.service
 */
@SpringBootTest(classes = LogServiceApplication.class)   //标识本类是一个SpringBootTest
@AutoConfigureMockMvc
public class LogConsumerListenerTest {
    private static final Logger logger = LoggerFactory.getLogger(LogConsumerListener.class);

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * @description 插入日志测试
     * @return void
     * @author Xianwei Wang
     * created at 11/18/20 1:08 PM
     */
    @Test
    public void sendLogMessageTest(){
        Log log = new Log();
        log.setUserId(Long.valueOf(1));
        log.setIp("127.0.0.1");
        log.setDesc("test");
        log.setGmtCreate(LocalDateTime.now());
        log.setPrivilegeId(Long.valueOf(1));
        log.setSuccess(Byte.valueOf((byte) 1));
        log.setDepartId(Long.valueOf(1));

        String json = JacksonUtil.toJson(log);
        Message message = MessageBuilder.withPayload(json).build();
        logger.info("sendLogMessage: message = " + message);
        rocketMQTemplate.sendOneWay("log-topic", message);
    }
}
