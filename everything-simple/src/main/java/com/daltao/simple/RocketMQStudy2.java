package com.daltao.simple;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

public class RocketMQStudy2 {
    public static void main(String[] args) throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer("daltao");
        producer.setNamesrvAddr("192.168.1.6:9876");
        producer.start();

        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            Message message = new Message("TopicTest", "TagB", "Order-" + i, ("Hello RocketMQ-" + i).getBytes(Charset.forName("utf8")));
            producer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    System.out.println(sendResult);
                    countDownLatch.countDown();
                }

                @Override
                public void onException(Throwable e) {
                    e.printStackTrace();
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();
        producer.shutdown();
    }
}
