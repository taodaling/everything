package com.daltao.zipkinweb;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@EnableAutoConfiguration
@RestController
@Slf4j
public class Backend {
    @RequestMapping("/api")
    public String printDate(@RequestHeader(name = "user-name", required = false) String username) {
        log.info("Receive request with username {}", username);
        if (username != null) {
            return new Date().toString() + " " + username;
        }
        return new Date().toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(Backend.class,
                "--spring.application.name=backend",
                "--server.port=9000");
    }
}
