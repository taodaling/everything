package com.daltao.consumer;

import com.daltao.api.DemoService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:consumer.xml"});
        context.start();
        // Obtaining a remote service proxy
        DemoService demoService = (DemoService)context.getBean("demoService");

        Map<Object, Object> param = new HashMap<>();
        param.put("name", "daltao");
        // Executing remote methods
        Map<Object, Object> hello = demoService.sayHello(param);
        // Display the call result
        System.out.println(hello);
    }
}