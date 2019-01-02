package com.daltao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException {
        new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
        while (true) {
            System.in.read();
        }
    }
}
