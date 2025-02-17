package com.example.rpc.client.config;

import com.example.rpc.client.util.ServiceProvider;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeansConfig {

    @Autowired
    private RpcClientConfiguration configuration;

    @Bean
    public ZkClient zkClient() {
        System.out.println("creating zkclient");
        return new ZkClient(configuration.getZkAddr(), configuration.getConnectTimeout());
    }

    /**
     * Google's Guava LoadingCache helps to maintain efficient and thread-safe caches.
     */
    @Bean
    public LoadingCache<String, List<ServiceProvider>> buildCache() {
        System.out.println("creating cache");
        return CacheBuilder.newBuilder()
                .build(new CacheLoader<String, List<ServiceProvider>>() {
                    @Override
                    public List<ServiceProvider> load(String key) throws Exception {
                        return null;
                    }
                });
    }
}
