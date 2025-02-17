package com.example.rpc.client.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to mark the service proxy field in the client, which is later injected by the
 * RpcClientBeanProcessor class. The inject instances are created by cglib in this example, while other proxy services
 * are also feasible for this project, such as jdk dynamic proxy.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServiceProxy {
    String value() default "";
    Class<?> interfaceClass() default void.class; // service interface class
    String interfaceName() default ""; // service interface name
    String version() default ""; // service version
    String group() default ""; // service group
}
