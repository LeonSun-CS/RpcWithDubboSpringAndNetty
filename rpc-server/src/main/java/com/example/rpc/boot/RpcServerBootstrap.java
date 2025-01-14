package com.example.rpc.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class RpcServerBootstrap {

    @Autowired
    private RpcServerRunner rpcServerRunner;

    @PostConstruct
    public void initRpcServer() {
        // this will be invoked after the bean is created, which will start the RPC server
        rpcServerRunner.run();
    }
}
