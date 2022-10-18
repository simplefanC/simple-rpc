package com.simplefanc.autoconfigure;

import com.simplefanc.extension.ExtensionLoader;
import com.simplefanc.factory.SingletonFactory;
import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.remoting.transport.RpcRequestTransport;
import com.simplefanc.remoting.transport.server.NettyRpcServer;
import com.simplefanc.serviceprovider.ServiceProvider;
import com.simplefanc.serviceprovider.impl.ZkServiceProviderImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chenfan
 * @date 2022/10/18 18:21
 **/
@Configuration
// 让 Spring 扫描被 @ConfigurationProperties 标注的类
@EnableConfigurationProperties(RpcProperties.class)
public class RpcAutoConfiguration {
    @Bean
    public NettyRpcServer rpcServer(RpcProperties rpcProperties) {
        return new NettyRpcServer(rpcProperties.getServerPort());
    }

    @Bean
    public DefaultRpcProcessor rpcProcessor(NettyRpcServer rpcServer) {
        return new DefaultRpcProcessor(rpcServer);
    }

    // 设置负载均衡
    @Bean
    public LoadBalance loadBalance(){
        return ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    // 设置服务发现
    @Bean
    public ServiceProvider serviceProvider(){
        // 可根据 RpcProperties 配置
        return new ZkServiceProviderImpl();
    }

    // 设置网络层实现
    @Bean
    public RpcRequestTransport rpcRequestTransport(){
        return ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    @Bean
    public SpringBeanPostProcessor beanPostProcessor(ServiceProvider serviceProvider, RpcRequestTransport rpcClient) {
        return new SpringBeanPostProcessor(serviceProvider, rpcClient);
    }

}
