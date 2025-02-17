package com.example.rpc.client.annotations;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation is used to mark the load balance strategy in the client, used to select the target service provider.
 */
@Component
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcLoadBalance {

    @AliasFor(annotation = Component.class)
    String value() default "";

    String strategy() default "random";
}
