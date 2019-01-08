package com.daltao.simple.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Consumer02 extends RuntimeExceptionConsumer<Channel> {
    private static final String QUEUE_NAME = "tasks";

    public static void main(String[] args) throws Exception {
        new BaseRabbitMQClient()
                .consume(new Consumer02());
    }

    @Override
    public void accept0(Channel channel) throws Exception {
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            try {
                for (char c : message.toCharArray()) {
                    if (c == '.') {
                        Thread.sleep(1000);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("[x] Received `" + message + "`");
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        channel.basicQos(1);
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
        });
        System.in.read();
    }
}
