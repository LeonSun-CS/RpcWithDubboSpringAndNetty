package com.example.rpc.client.boot;

import com.example.rpc.client.annotations.RpcServiceProxy;
import com.example.rpc.client.proxies.RpcServiceProxyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * This class is used to inject proxy beans for services annotated with @RpcServiceProxy. This is done by implementing
 * the BeanPostProcessor interface. Another AOP representation.
 * The proxy will intercept all methods invoked on the service and send the request to the server.
 */
@Component
@Slf4j
public class RpcClientBeanProcessor implements BeanPostProcessor {

    @Autowired
    private RpcServiceProxyFactory rpcServiceProxyFactory;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // check if the bean has a field annotated with @RpcServiceProxy; inject a proxy instance if so.
        Field[] declaredFields = bean.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            if (!field.isAccessible()) field.setAccessible(true);
            if (field.isAnnotationPresent(RpcServiceProxy.class)) {
                // use cglib to create a proxy instance
                Object proxyInstance = rpcServiceProxyFactory.newProxyInstance(field.getType());
                if (proxyInstance != null) {
                    try {
                        field.set(bean, proxyInstance);
                    } catch (IllegalAccessException e) {
                        log.error("Proxy instance info: {}", proxyInstance);
                        log.error("Failed to inject proxy instance for field: {}", field.getName());
                    }
                } else {
                    log.error("Proxy instance info: {}", proxyInstance);
                    log.error("Failed to create proxy instance for field: {}", field.getName());
                }
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
