package com.daltao.simple;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;

import java.nio.charset.Charset;
import java.util.List;

public class RocketMQStudy5 {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("daltao");
        producer.setNamesrvAddr("192.168.1.6:9876");
        producer.start();

        for (int i = 0; i < 100; i++) {
            int orderId = i % 10;
            Message msg = new Message("TopicTest", "TagF", "KEY-" + i, ("Order-" + orderId + "-" + (i / 10 + 1)).getBytes(Charset.forName("utf8")));
            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                    Integer id = (Integer) arg;
                    int index = id % mqs.size();
                    return mqs.get(index);
                }
            }, orderId);

            System.out.printf("%s%n", new String(msg.getBody(), Charset.forName("utf8")));
        }

        producer.shutdown();
    }
}
