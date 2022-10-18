package com.simplefanc.autoconfigure.annotation;


import java.lang.annotation.*;

/**
 * 消费服务
 * RPC reference annotation, autowire the service implementation class
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
