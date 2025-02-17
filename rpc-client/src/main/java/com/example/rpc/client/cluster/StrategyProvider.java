package com.example.rpc.client.cluster;

import com.example.rpc.client.annotations.RpcLoadBalance;
import com.example.rpc.client.config.RpcClientConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * This interface selects a LB strategy class based on configuration.
 */
@Component
@Slf4j
public class StrategyProvider implements ApplicationContextAware {

    @Autowired
    private RpcClientConfiguration clientConfiguration;

    @Getter
    LoadBalanceStrategy strategy;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // Get all beans with the HrpcLoadBalance annotation
        // Select the strategy based on the configuration
        applicationContext.getBeansWithAnnotation(RpcLoadBalance.class).values().forEach(bean -> {
            RpcLoadBalance loadBalance = bean.getClass().getAnnotation(RpcLoadBalance.class);
            if (clientConfiguration.getRpcClientClusterStrategy().equals(loadBalance.strategy())) {
                strategy = (LoadBalanceStrategy) bean;
            }
        });
    }
}
