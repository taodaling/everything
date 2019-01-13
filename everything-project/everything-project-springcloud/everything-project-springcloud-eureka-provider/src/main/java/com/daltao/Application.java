package com.daltao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@EnableEurekaClient
@RestController
public class Application {
    @Value("${server.port}")
    private Integer port;

    @Configuration
    public static class Config {
        private static AtomicInteger inc = new AtomicInteger();

        @Bean(name = "incNumber")
        @Scope("prototype")
        public Integer incNumber() {
            return inc.incrementAndGet();
        }
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }

    @Resource
    ApplicationContext context;

    @RequestMapping(value = "/inc", method = RequestMethod.GET)
    public String inc() {
        return "" + context.getBean("incNumber");
    }

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String index() throws InterruptedException {
        return Integer.toString(new Random().nextInt());
    }

}
