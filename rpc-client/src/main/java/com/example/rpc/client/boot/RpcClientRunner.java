package com.example.rpc.client.boot;

import com.example.rpc.client.servicediscovery.RpcServiceDiscovery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RpcClientRunner {

    @Autowired
    private RpcServiceDiscovery serviceDiscovery;

    /**
     * service discovery
     */
    public void run() {
        serviceDiscovery.serviceDiscovery();
    }
}
