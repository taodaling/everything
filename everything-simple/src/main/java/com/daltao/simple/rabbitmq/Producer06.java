package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;

import java.util.Objects;
import java.util.UUID;

public class Producer06 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "fib";
    private static final String TASK_QUEUE = "task";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Producer06());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE, queueName);

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        channel.basicConsume(queueName, true, (tag, delivery) -> {
            if (!Objects.equals(uuid, delivery.getProperties().getCorrelationId())) {
                return;
            }
            String body = new String(delivery.getBody());
            System.out.println("remote server response with `" + body + "`");
        }, (tag) -> {
        });

        channel.basicPublish(EXCHANGE, TASK_QUEUE, new AMQP.BasicProperties.Builder()
                        .replyTo(queueName).correlationId(uuid).build(),
                "10".getBytes());

        System.out.println("[x] Sent request");
        System.in.read();
    }
}