package cn.edu.xmu.log.service.mq;

import cn.edu.xmu.log.dao.LogDao;
import cn.edu.xmu.log.mapper.LogPoMapper;
import cn.edu.xmu.log.model.bo.Log;
import cn.edu.xmu.ooad.util.JacksonUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 日志消费者
 * @author Xianwei Wang
 */
@Service
@RocketMQMessageListener(topic = "log-topic", consumerGroup = "log-group")
public class LogConsumerListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    @Autowired
    private LogDao logDao;

    private static final Logger logger = LoggerFactory.getLogger(LogConsumerListener.class);

    @Override
    public void onMessage(String message) {
        Log log = JacksonUtil.toObj(message, Log.class);
        logger.debug("onMessage: got message log =" + log);
        logDao.insertLog(log);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        logger.info("prepareStart: consumergroup =" + defaultMQPushConsumer.getConsumerGroup());
    }
}
