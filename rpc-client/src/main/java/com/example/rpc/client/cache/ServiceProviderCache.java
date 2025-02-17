package com.example.rpc.client.cache;

import com.example.rpc.client.util.ServiceProvider;

import java.util.List;

public interface ServiceProviderCache {
    void put(String key, List<ServiceProvider> value);

    List<ServiceProvider> get(String key);

    void evict(String key);

    void update(String key,List<ServiceProvider> value);
}
