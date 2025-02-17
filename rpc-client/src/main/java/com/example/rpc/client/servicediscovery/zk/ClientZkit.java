package com.example.rpc.client.servicediscovery.zk;

import com.example.rpc.client.cache.ServiceProviderCache;
import com.example.rpc.client.config.RpcClientConfiguration;
import com.google.common.collect.Lists;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.rpc.client.util.ServiceProvider;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientZkit {

    @Autowired
    private RpcClientConfiguration configuration;

    @Autowired
    private ServiceProviderCache cache;

    @Autowired
    private ZkClient zkClient; // initialized in RpcClientConfiguration

    public List<String> getServiceList() {
        String path = configuration.getZkRoot();
        return zkClient.getChildren(path);
    }

    public List<ServiceProvider> getServiceProviders(String serviceName) {
        String path = configuration.getZkRoot() + "/" + serviceName;
        return convertToProviderService(serviceName, zkClient.getChildren(path));
    }

    /**
     * This method subscribes to the Zookeeper event for the given service name, and updates the cache with the
     * new service provider information. When new service providers are added or removed, the cache is updated.
     * In short, this is a watcher method.
     */
    public void subscribeZkEvent(String serviceName) {
        String path = configuration.getZkRoot() + "/" + serviceName;
        zkClient.subscribeChildChanges(path, new IZkChildListener() {
            @Override
            public void handleChildChange(String s, List<String> list) throws Exception {
                if (list != null && !list.isEmpty()) {
                    List<ServiceProvider> providerServices = convertToProviderService(serviceName, list);
                    cache.update(serviceName, providerServices);
                }
            }

            ;
        });
    }

    private List<ServiceProvider> convertToProviderService(String serviceName, List<String> providerStringList) {
        if (providerStringList == null || providerStringList.isEmpty()) {
            return Lists.newArrayListWithCapacity(0);
        }
        return providerStringList.stream().map(providerString -> {
            System.out.println("***found service: " + serviceName + " @ " + providerString);
            String[] split = providerString.split(":");
            return ServiceProvider.builder()
                    .serviceName(serviceName)
                    .serverIp(split[0])
                    .rpcPort(Integer.parseInt(split[1])) // TODO: **customizable** more info can be added when needed
                    .build();
        }).collect(Collectors.toList());
    }

}
