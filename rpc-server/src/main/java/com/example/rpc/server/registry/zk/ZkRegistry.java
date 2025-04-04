package com.example.rpc.server.registry.zk;

import com.example.rpc.annotations.MyRpcService;
import com.example.rpc.server.config.RpcServerConfiguration;
import com.example.rpc.server.registry.RpcRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ZkRegistry implements RpcRegistry, ApplicationContextAware {

    @Autowired
    private ServerZKit zKit;

    @Autowired
    private RpcServerConfiguration configuration;

//    private final Logger logger = LoggerFactory.getLogger(ZkRegistry.class);

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void serviceRegistry() {
        // register root node if necessary
        // get the beans with the @MyRpcService annotation; then, based on the interfaceClass attribute, we can
        // determine which interface to register with the registry; register service nodes if not already created,
        // and then register provider nodes

        zKit.createRootNode();

        applicationContext.getBeansWithAnnotation(MyRpcService.class).values().forEach(bean -> {
            MyRpcService myRpcService = bean.getClass().getAnnotation(MyRpcService.class);
//            System.err.println("got bean " + myRpcService.toString());
            Class<?> interfaceClass = myRpcService.interfaceClass();
            String serviceName = interfaceClass.getName();
            zKit.createPersistentNode(serviceName);
            String providerNode = serviceName + "/" + configuration.getServerAddress() + ":" + configuration.getRpcPort();
            zKit.createNode(providerNode);
            System.err.println("Registered service: serviceName={"+serviceName+"}, providerNode={"+providerNode+"}");
            log.info("Registered service: serviceName={}, providerNode={}", serviceName, providerNode);
        });
    }
}
