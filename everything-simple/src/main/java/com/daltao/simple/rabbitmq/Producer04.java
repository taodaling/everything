package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;

public class Producer04 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "direct-logs";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Producer04());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "direct");
        String info = "info: Hello World!";
        String warn = "warn: Hello World!";
        String error = "error: Hello World!";
        channel.basicPublish(EXCHANGE, "info", null, info.getBytes());
        channel.basicPublish(EXCHANGE, "warn", null, warn.getBytes());
        channel.basicPublish(EXCHANGE, "error", null, error.getBytes());
        System.out.println("[x] Sent `" + "all" + "`");
    }
}