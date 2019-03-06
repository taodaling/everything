package com.daltao.zipkinweb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@EnableAutoConfiguration
@RestController
@CrossOrigin
public class Frontend {
    private RestTemplate restTemplate;

    public Frontend(@Autowired RestTemplateBuilder builder) {
        restTemplate = builder.build();
    }

    @RequestMapping("/")
    public String callBackend() {
        return restTemplate.getForObject("http://localhost:9000/api", String.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Frontend.class,
                "--spring.application.name=frontend",
                "--server.port=9001");
    }
}
