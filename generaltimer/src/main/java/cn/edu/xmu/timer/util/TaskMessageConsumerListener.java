package cn.edu.xmu.timer.util;

import cn.edu.xmu.ooad.util.JacksonUtil;
import cn.edu.xmu.timer.model.bo.TaskMessage;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * TaskMessage的消费者
 * @author ：Zeyao Feng
 * @date ：Created in 2020-12-02 19:49
 */
@Service
@RocketMQMessageListener(topic = "1", selectorExpression = "1", consumeMode = ConsumeMode.CONCURRENTLY, consumeThreadMax = 10, consumerGroup = "log-group")
public class TaskMessageConsumerListener implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(TaskMessageConsumerListener.class);

    @Override
    public void onMessage(String s) {
        TaskMessage taskMessage = JacksonUtil.toObj(s, TaskMessage.class);
        logger.info("onMessage: got message TaskMessage =" + taskMessage.toString());
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        logger.info("prepareStart: consumergroup =" + defaultMQPushConsumer.getConsumerGroup());
    }
}