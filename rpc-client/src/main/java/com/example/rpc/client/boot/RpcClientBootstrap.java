package com.example.rpc.client.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RpcClientBootstrap {

         @Autowired
         private RpcClientRunner rpcClientRunner;

         @PostConstruct
         public void initRpcClient() {
             System.out.println("entered post construct method");
             rpcClientRunner.run();
         }
}
