package com.example.rpc.boot;

import com.example.rpc.netty.RpcServer;
import com.example.rpc.registry.RpcRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * the run method will be invoked by the RpcServerBootstrap class, which is annotated with @PostConstruct.
 * This class will implement the logic of RPC server:
 * 1. Register the services
 * 2. Start the server
 */
@Component
public class RpcServerRunner {

    @Autowired
    private RpcRegistry registry; // In this example, we use Zookeeper as the registry
    @Autowired
    private RpcServer server;

    public void run() {
        // 1. service registration follows this format:
        // /rpc (layer I) >> /com.example.rpc.service.SomeService (layer II) >> /address:port (layer III)
        registry.serviceRegistry();
        // 2. start the server, based on Netty, to listen to the port and accept connections and
        // process requests
        server.start();
    }
}
