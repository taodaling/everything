package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Consumer06 extends RuntimeExceptionConsumer<Channel> {
    private static final String EXCHANGE = "fib";
    private static final String TASK_QUEUE = "task";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Consumer06());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.exchangeDeclare(EXCHANGE, "direct");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE, TASK_QUEUE);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            int k = Integer.parseInt(message);
            int a0 = 0;
            int a1 = 1;
            for (int i = 0; i < k; i++) {
                int tmp = a0;
                a0 = a1;
                a1 = a1 + tmp;
            }
            channel.basicPublish(EXCHANGE, delivery.getProperties().getReplyTo(), new AMQP.BasicProperties.Builder().correlationId(delivery.getProperties().getCorrelationId()).build(),
                    Integer.toString(a0).getBytes());
            System.out.println("[x] Received `" + message + "` and send `" + a0 + "`");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
        System.in.read();
    }
}