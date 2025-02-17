package com.example.rpc.client.proxies;

import com.example.rpc.client.rpccommunication.RpcRequestManager;
import com.example.rpc.messages.RpcRequest;
import com.example.rpc.messages.RpcResponse;
import org.springframework.beans.BeansException;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.UUID;

@Component
public class RpcServiceProxyFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public <T> T newProxyInstance(Class<T> cls) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(cls);
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * intercept methods called on the proxy object, and process it through rpc via Netty.
             */
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                // the object methods, such as toString, hashCode, equals, etc., need not to be intercepted and can
                // pass through
                if (method.getDeclaringClass().equals(Object.class)) {
                    return method.invoke(method.getDeclaringClass().newInstance(), objects);
                }
                // make the RPC call
                RpcRequest request = RpcRequest
                        .builder()
                        .requestId(UUID.randomUUID().toString())
                        .className(method.getDeclaringClass().getName())
                        .methodName(method.getName())
                        .parameterTypes(method.getParameterTypes())
                        .parameters(objects)
                        .build();
                // send the request to the server, using the RpcRequestManager bean
                RpcRequestManager rpcRequestManager = applicationContext.getBean(RpcRequestManager.class);
                if (rpcRequestManager == null) {
                    throw new RuntimeException("Spring IOC exception");
                }

                // todo: for debugging, delete later
                RpcResponse rpcResponse = rpcRequestManager.sendRequest(request);
                System.out.println("rpcResponse = " + rpcResponse);

                return rpcResponse.getResult();
            }
        });
        return (T) enhancer.create();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
