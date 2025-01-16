package com.example.rpc.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * this is to start the entire server application, which will start the rpc server through
 * Spring's @PostConstruct annotation on RpcServerBootstrap class
 */
@SpringBootApplication(scanBasePackages = "com.example.rpc")
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
