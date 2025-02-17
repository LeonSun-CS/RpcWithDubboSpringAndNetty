package com.example.rpc.client.servicediscovery.zk;

import com.example.rpc.client.cache.ServiceProviderCache;
import com.example.rpc.client.servicediscovery.RpcServiceDiscovery;
import com.example.rpc.client.util.ServiceProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ZkServiceDiscovery implements RpcServiceDiscovery {
    @Autowired
    private ClientZkit clientZkit;

    @Autowired
    private ServiceProviderCache cache;


    @Override
    public void serviceDiscovery() {
        // get all service names
        List<String> serviceList = clientZkit.getServiceList();
        serviceList.forEach(serviceName -> {
            List<ServiceProvider> providers = clientZkit.getServiceProviders(serviceName);
            cache.put(serviceName, providers);
            clientZkit.subscribeZkEvent(serviceName); // watch for changes
            log.info("Received and Subscribed service name: {}, providers: {}", serviceName, providers);
        });
    }
}
