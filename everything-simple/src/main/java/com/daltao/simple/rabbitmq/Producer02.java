package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

public class Producer02 extends RuntimeExceptionConsumer<Channel> {
    private static final String QUEUE_NAME = "tasks";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Producer02());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, "Hello......".getBytes());
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, "Hello".getBytes());
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, "Hello".getBytes());
        channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, "Hello".getBytes());
    }
}
