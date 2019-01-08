package com.daltao.simple.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class RocketMQStudy11 {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("daltao");
        producer.setNamesrvAddr("192.168.1.6:9876");
        producer.setRetryTimesWhenSendFailed(0);
        producer.start();

        String topic = "TopicTest";
        Charset charset = Charset.forName("utf8");
        List<Message> messageList = Arrays.asList(
                new Message(topic, "BatchTest", "OrderID001", "Hello RocketMQ-1".getBytes(charset)),
                new Message(topic, "BatchTest", "OrderID002", "Hello RocketMQ-2".getBytes(charset)),
                new Message(topic, "BatchTest", "OrderID003", "Hello RocketMQ-3".getBytes(charset))
        );
        producer.send(messageList);

        producer.shutdown();
    }
}
