package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class BaseRabbitMQClient {

    private static final String HOST = "192.168.1.6";
    private static final String USERNAME = "rabbitmq";
    private static final String PASSWORD = "rabbitmq";

    public void consume(Consumer<Channel> channelConsumer) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channelConsumer.accept(channel);
        }
    }
}
