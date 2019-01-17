package com.daltao.simple.kafka;

public class KafkaConstant {
    public static final String HOST = "virtualbox.com:9094";
    public static final int MESSAGE_COUNT = 100;
    public static String CLIENT_ID = "client_1";
    public static String TOPIC = "demo";
    public static String GROUP_ID = "group_1";
    public static int MAX_NO_MESSAGE_FOUND_COUNT = 100;
    public static String OFFSET_RESET_LATEST = "latest";
    public static String OFFSET_RESET_EARLIER = "earliest";
    public static int MAX_POLL_RECORDS = 1;
}
