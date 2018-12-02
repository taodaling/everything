package com.daltao.simple;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class RocketMQStudy4 {
    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("daltao");
        consumer.setNamesrvAddr("192.168.1.6:9876");
        consumer.subscribe("TopicTest", "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                System.out.println(MessageFormat.format("{0} received message - {1} ", Thread.currentThread().getName(), msgs.stream().map(x -> new String(x.getBody(), Charset.forName("utf8"))).collect(Collectors.toList())));
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        consumer.start();
        System.out.println("Start consuming");
    }
}
