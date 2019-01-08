package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;

public class Producer01 extends RuntimeExceptionConsumer<Channel> {
    private static final String QUEUE_NAME = "test01";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Producer01());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "Hello World!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println("[x] Sent `" + message + "`");
    }
}
