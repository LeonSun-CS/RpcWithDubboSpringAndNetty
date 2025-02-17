package com.example.rpc.client.cluster;

import com.example.rpc.client.util.ServiceProvider;

import java.util.List;

/**
 * Will be implemented as concrete LB strategies, such as Random, RoundRobin, etc. in the lb package.
 */
public interface LoadBalanceStrategy {
    /**
     * This method selects a service provider based on the corresponding strategy.
     */
    ServiceProvider select(List<ServiceProvider> serviceProviders);
}
