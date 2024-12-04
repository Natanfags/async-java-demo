package com.example.asyncdemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfiguration {

    @Bean(name = "limitedThreadPool")
    public ExecutorService threadPool() {
        return Executors.newFixedThreadPool(4);
    }
}
