package com.daltao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:provider.xml"})
public class MainConfiguration {
}
