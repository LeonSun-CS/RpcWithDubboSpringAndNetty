package com.example.rpc.client.cache.impls;

import com.example.rpc.client.cache.ServiceProviderCache;
import com.example.rpc.client.util.ServiceProvider;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class DefaultLocalServiceCache implements ServiceProviderCache {

    @Autowired
    private LoadingCache<String, List<ServiceProvider>> cache;

    @Override
    public void put(String key, List<ServiceProvider> value) {
        cache.put(key, value);
    }

    @Override
    public List<ServiceProvider> get(String key) {
        try {
            return cache.get(key);
        } catch (ExecutionException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void evict(String key) {
        cache.invalidate(key);
    }

    @Override
    public void update(String key, List<ServiceProvider> value) {
        evict(key);
        put(key, value);
    }
}
