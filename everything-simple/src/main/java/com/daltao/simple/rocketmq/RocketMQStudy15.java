package com.daltao.simple.rocketmq;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.nio.charset.Charset;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class RocketMQStudy15 {
    public static void main(String[] args) throws Exception {
        TransactionListener transactionListener = new TransactionListener() {
            @Override
            public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
                System.out.println("SUCCESS");
                return LocalTransactionState.UNKNOW;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt msg) {
                System.out.println("CHECK " + new String(msg.getBody(), Charset.forName("utf8")));
                return LocalTransactionState.COMMIT_MESSAGE;
            }
        };

        TransactionMQProducer producer = new TransactionMQProducer("daltao");
        producer.setNamesrvAddr("192.168.1.6:9876");
        producer.setTransactionListener(transactionListener);
        producer.setExecutorService(Executors.newFixedThreadPool(4));
        producer.start();

        producer.sendMessageInTransaction(new Message("TopicTest", "transaction", ("1").getBytes(Charset.forName("utf8"))), null);
        producer.sendMessageInTransaction(new Message("TopicTest", "transaction", ("2").getBytes(Charset.forName("utf8"))), null);
        Thread.sleep(30000);
        producer.shutdown();
    }
}
