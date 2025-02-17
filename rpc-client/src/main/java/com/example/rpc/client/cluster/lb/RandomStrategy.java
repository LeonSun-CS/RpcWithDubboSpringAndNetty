package com.example.rpc.client.cluster.lb;

import com.example.rpc.client.annotations.RpcLoadBalance;
import com.example.rpc.client.cluster.LoadBalanceStrategy;
import com.example.rpc.client.util.ServiceProvider;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;

@RpcLoadBalance(strategy = "random")
public class RandomStrategy implements LoadBalanceStrategy {
    @Override
    public ServiceProvider select(List<ServiceProvider> serviceProviders) {
        int len = serviceProviders.size();
        int index = RandomUtils.nextInt(0,len);
        return serviceProviders.get(index);
    }
}
