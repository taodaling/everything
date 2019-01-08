package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Consumer03 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "logs";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Consumer03());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE, "");
        System.out.println("Create a queue named " + queueName);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println("[x] Received `" + message + "`");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        System.in.read();
    }
}
