package com.daltao.simple;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RocketMQStudy6 {
    public static void main(String[] args) throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("daltao");
        consumer.setNamesrvAddr("192.168.1.6:9876");
        consumer.subscribe("TopicTest", "TagF");
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        AtomicInteger consumeTimes = new AtomicInteger(0);
        consumer.registerMessageListener(new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(true);
                int t = consumeTimes.incrementAndGet();
                System.out.printf("%s received new message status %d: %s%n", Thread.currentThread().getName(), t % 2, msgs.stream().map(x -> new String(x.getBody(), Charset.forName("utf8"))).collect(Collectors.toList()));
                switch (t % 2) {
                    case 0:
                        return ConsumeOrderlyStatus.SUCCESS;
                    case 1:
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    default:
                        return ConsumeOrderlyStatus.SUCCESS;
                }
            }
        });

        consumer.start();
        System.out.println("Start consuming");
    }
}
