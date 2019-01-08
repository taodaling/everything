package com.daltao;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheKey;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Random;

@SpringCloudApplication
@RestController
public class Application {
    @Configuration
    public static class Config {
        @Bean
        public HystrixMetricsStreamServlet hystrixMetricsStreamServlet() {
            return new HystrixMetricsStreamServlet();
        }

        @Bean
        public ServletRegistrationBean registrationOfHystrixMetricsStreamServlet(HystrixMetricsStreamServlet hystrixMetricsStreamServlet) {
            ServletRegistrationBean result = new ServletRegistrationBean();
            result.setServlet(hystrixMetricsStreamServlet);
            result.setEnabled(true);
            result.addUrlMappings("/hystrix.stream");
            return result;
        }
    }

    @Resource
    Service service;

    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public String hello() {
        return service.hello();
    }


    @org.springframework.stereotype.Service
    public static class Service {
        @HystrixCommand(fallbackMethod = "helloFallback")
        public String hello() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "ok";
        }

        public String helloFallback() {
            return "error";
        }
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
    }
}
