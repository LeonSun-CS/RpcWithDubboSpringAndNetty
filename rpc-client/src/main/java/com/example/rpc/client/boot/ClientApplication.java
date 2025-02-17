package com.example.rpc.client.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example.rpc.client")
//@SpringBootApplication
public class ClientApplication {
    public static void main(String[] args) {
//        SpringApplication.run(ClientApplication.class, args);
        System.out.println("Starting application...");
        try {
            SpringApplication.run(ClientApplication.class, args);
        } catch (Exception e) {
            System.err.println("Failed to start application:");
            e.printStackTrace();
        }
    }
}
