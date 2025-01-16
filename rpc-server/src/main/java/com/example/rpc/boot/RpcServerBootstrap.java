package com.example.rpc.boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class RpcServerBootstrap {

    @Autowired
    private RpcServerRunner rpcServerRunner;

    @PostConstruct
    public void initRpcServer() {
        // this will be invoked after the bean is created, which will start the RPC server
        rpcServerRunner.run();
    }
}
