package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Consumer04 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "direct-logs";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Consumer04());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE, "error");
        channel.queueBind(queueName, EXCHANGE, "warn");
        System.out.println("Create a queue named " + queueName);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("[x] Received `" + message + "`");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        System.in.read();
    }
}
