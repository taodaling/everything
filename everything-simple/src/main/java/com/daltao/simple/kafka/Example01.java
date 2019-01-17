package com.daltao.simple.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.KafkaAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Example01 {
    public static Producer<Long, String> createProducer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.HOST);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaConstant.CLIENT_ID);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, CustomPartitioner.class);
        return new KafkaProducer<Long, String>(properties);
    }

    public static AdminClient createAdmin() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.HOST);
        properties.put(AdminClientConfig.CLIENT_ID_CONFIG, KafkaConstant.CLIENT_ID);
        return KafkaAdminClient.create(properties);
    }

    public static class CustomPartitioner implements Partitioner {
        private static final int PARTITION_COUNT = 1;

        @Override
        public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
            return (int) (((long) key) % PARTITION_COUNT);
        }

        @Override
        public void close() {

        }

        @Override
        public void configure(Map<String, ?> configs) {

        }
    }

    public static Consumer<Long, String> createConsumer() {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaConstant.HOST);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.GROUP_ID);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, KafkaConstant.MAX_POLL_RECORDS); //How many records should client fetch in one iteration
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false"); //WHen the consumer from a group receives a message it must commit the offset of that record.
        /**
         * For each consumer group, the last committed offset value is stored.
         * This configuration comes handy if no offset is committed for that group, i.e. it is a new created group.
         * - Setting this value to earliest will cause the consumer to fetch records from the beginning of offset.
         * - Setting this value to latest will cause the consumer to fetch records from the new record. By new records
         * mean those created after the consumer group active
         */
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, KafkaConstant.OFFSET_RESET_EARLIER);
        Consumer<Long, String> consumer = new KafkaConsumer<Long, String>(properties);
        consumer.subscribe(Collections.singletonList(KafkaConstant.TOPIC));
        return consumer;
    }

    public static class ConsumerTask {
        public static void main(String[] args) {
            Consumer<Long, String> consumer = createConsumer();

            while (true) {
                ConsumerRecords<Long, String> consumerRecords = consumer.poll(Duration.ofSeconds(3));
                if (consumerRecords.isEmpty()) {
                    continue;
                }
                consumerRecords.forEach(record -> {
                    System.out.println("Record key " + record.key());
                    System.out.println("Record value " + record.value());
                    System.out.println("Record partition " + record.partition());
                    System.out.println("Record offset " + record.offset());
                });
                consumer.commitAsync();
            }
        }
    }

    public static class AdminTask {
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            AdminClient client = createAdmin();
            client.deleteTopics(Collections.singletonList(KafkaConstant.TOPIC)).all().get();
            client.createTopics(Collections.singleton(new NewTopic(KafkaConstant.TOPIC, 5, (short) 1)))
                    .all().get();
        }
    }

    public static class ProducerTask {
        public static void main(String[] args) throws ExecutionException, InterruptedException {
            Producer<Long, String> producer = createProducer();
            for (int i = 0; i < 10; i++) {
                ProducerRecord<Long, String> record = new ProducerRecord<>(KafkaConstant.TOPIC, (long) i, "This is record " + i);
                producer.send(record, (recordMetadata, e) -> {
                    System.out.println("Send record " + record);
                });
            }
            producer.flush();
            producer.close();
        }
    }
}
