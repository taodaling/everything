package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;

public class Producer05 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "topic-logs";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Producer05());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "topic");
        String info = "info: Hello World!";
        String warn = "warn: Hello World!";
        String error = "error: Hello World!";
        channel.basicPublish(EXCHANGE, "auth.info", null, info.getBytes());
        channel.basicPublish(EXCHANGE, "auth.warn", null, warn.getBytes());
        channel.basicPublish(EXCHANGE, "auth.error", null, error.getBytes());
        channel.basicPublish(EXCHANGE, "task.info", null, info.getBytes());
        channel.basicPublish(EXCHANGE, "task.warn", null, warn.getBytes());
        channel.basicPublish(EXCHANGE, "task.error", null, error.getBytes());
        System.out.println("[x] Sent `" + "all" + "`");
    }
}