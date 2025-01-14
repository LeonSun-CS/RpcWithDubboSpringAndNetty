package com.example.rpc.annotations;

import org.springframework.stereotype.Component;

/**
 * This is a custom annotation to mark the service class, so that our spring application can scan it
 * and register it to a registry service.
 */
@Component
public @interface MyRpcService {

        /**
        * The service interface class.
        */
        Class<?> interfaceClass() default void.class;

        /**
        * The service interface name.
        */
        String interfaceName() default "";

        /**
        * The service version.
        */
        String version() default "";

        /**
        * The service group.
        */
        String group() default "";
}
