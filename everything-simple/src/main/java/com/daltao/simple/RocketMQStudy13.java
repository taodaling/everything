package com.daltao.simple;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class RocketMQStudy13 {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("daltao");
        producer.setNamesrvAddr("192.168.1.6:9876");
        producer.setRetryTimesWhenSendFailed(0);
        producer.start();

        String topic = "TopicTest";
        Charset charset = Charset.forName("utf8");
        Message msg1 = new Message(topic, "SqlTest", "OrderID001", "Hello RocketMQ-1".getBytes(charset));
        msg1.putUserProperty("sequence", "1");
        msg1.putUserProperty("tag", "SqlTest");
        Message msg2 = new Message(topic, "SqlTest", "OrderID002", "Hello RocketMQ-2".getBytes(charset));
        msg2.putUserProperty("sequence", "2");
        msg2.putUserProperty("tag", "SqlTest");
        Message msg3 = new Message(topic, "SqlTest", "OrderID003", "Hello RocketMQ-3".getBytes(charset));
        msg3.putUserProperty("sequence", "3");
        msg3.putUserProperty("tag", "SqlTest");
        List<Message> messageList = Arrays.asList(
                msg1, msg2, msg3
        );
        producer.send(messageList);

        producer.shutdown();
    }
}
