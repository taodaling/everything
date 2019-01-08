package com.daltao.simple.rocketmq;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.Charset;

public class RocketMQStudy9 {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("daltao");
        producer.setNamesrvAddr("192.168.1.6:9876");
        producer.setRetryTimesWhenSendFailed(0);
        producer.start();
        for (int i = 0; i < 100; i++) {
            Message message = new Message("TopicTest", "TagF", ("Hello RocketMQ-" + i).getBytes(Charset.forName("utf8")));
            message.setDelayTimeLevel(3);
            SendResult result = producer.send(message);
            System.out.println(result);
        }

        producer.shutdown();
    }
}
