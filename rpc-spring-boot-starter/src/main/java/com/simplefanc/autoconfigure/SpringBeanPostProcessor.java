package com.simplefanc.autoconfigure;

import com.simplefanc.autoconfigure.annotation.RpcReference;
import com.simplefanc.autoconfigure.annotation.RpcService;
import com.simplefanc.config.RpcServiceConfig;
import com.simplefanc.extension.ExtensionLoader;
import com.simplefanc.factory.SingletonFactory;
import com.simplefanc.proxy.RpcClientProxy;
import com.simplefanc.remoting.transport.RpcRequestTransport;
import com.simplefanc.serviceprovider.ServiceProvider;
import com.simplefanc.serviceprovider.impl.ZkServiceProviderImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

/**
 * @author chenfan
 * @date 2022/10/18 18:54
 **/
@Slf4j
public class SpringBeanPostProcessor implements BeanPostProcessor {
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor(ServiceProvider serviceProvider, RpcRequestTransport rpcClient) {
        this.serviceProvider = serviceProvider;
        this.rpcClient = rpcClient;
    }

    /**
     * 实例化之前会调用
     * 判断类上是否有 RpcService 注解。
     * 如果有的话，就取出 group 和 version 的值。
     * 然后，再调用 ServiceProvider 的 publishService() 方法发布服务即可！
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    /**
     * 实例化之后会调用
     * 遍历类的属性上是否有 RpcReference 注解。如果有的话，我们就通过反射将这个属性赋值即可！
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    // 反射赋值
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
