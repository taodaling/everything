package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;

public class Producer03 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "logs";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Producer03());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "fanout");
        String message = "info: Hello World!";
        channel.basicPublish(EXCHANGE, "", null, message.getBytes());
        System.out.println("[x] Sent `" + message + "`");
    }
}