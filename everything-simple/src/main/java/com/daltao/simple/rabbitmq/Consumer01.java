package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Consumer01 extends RuntimeExceptionConsumer<Channel> {
    private static final String QUEUE_NAME = "test01";
    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Consumer01());
    }
    @Override
    public void accept0(Channel channel) throws Exception {
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("[x] Received `" + message + "`");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        System.in.read();
    }
}
