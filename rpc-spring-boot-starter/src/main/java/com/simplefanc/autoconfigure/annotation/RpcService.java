package com.simplefanc.autoconfigure.annotation;


import java.lang.annotation.*;

/**
 * 注册服务
 * RPC service annotation, marked on the service implementation class
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}
