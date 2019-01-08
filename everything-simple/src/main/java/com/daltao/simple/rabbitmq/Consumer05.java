package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Consumer05 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "topic-logs";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Consumer05());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "topic");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE, "*.error");
        channel.queueBind(queueName, EXCHANGE, "task.*");
        System.out.println("Create a queue named " + queueName);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("[x] Received `" + message + "`");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        System.in.read();
    }
}