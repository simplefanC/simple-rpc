package com.simplefanc.autoconfigure;

import com.simplefanc.extension.ExtensionLoader;
import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.provider.ServiceProvider;
import com.simplefanc.registry.ServiceDiscovery;
import com.simplefanc.registry.ServiceRegistry;
import com.simplefanc.remoting.transport.client.NettyRpcClient;
import com.simplefanc.remoting.transport.client.RpcRequestTransport;
import com.simplefanc.remoting.transport.server.NettyRpcServer;
import com.simplefanc.remoting.transport.server.handler.RpcRequestHandler;
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
    public DefaultRpcProcessor rpcProcessor(NettyRpcServer rpcServer) {
        return new DefaultRpcProcessor(rpcServer);
    }

    @Bean
    public SpringBeanPostProcessor beanPostProcessor(ServiceProvider serviceProvider, ServiceRegistry serviceRegistry, RpcProperties rpcProperties, RpcRequestTransport rpcClient) {
        return new SpringBeanPostProcessor(serviceProvider, serviceRegistry, rpcProperties.getServerPort(), rpcClient);
    }

    @Bean
    public NettyRpcServer rpcServer(RpcProperties rpcProperties, ServiceProvider serviceProvider) {
        RpcRequestHandler requestHandler = new RpcRequestHandler(serviceProvider);
        return new NettyRpcServer(rpcProperties.getServerPort(), requestHandler, rpcProperties.getSerialization(), rpcProperties.getCompress());
    }

    @Bean
    public ServiceRegistry serviceRegistry(RpcProperties rpcProperties) {
        return ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension(rpcProperties.getRegistryProtocol());
    }

    /**
     * 服务发布(服务端)
     * @param rpcProperties
     * @return
     */
    @Bean
    public ServiceProvider serviceProvider(RpcProperties rpcProperties) {
        return ExtensionLoader.getExtensionLoader(ServiceProvider.class).getExtension(rpcProperties.getRegistryProtocol());
    }

    /**
     * 设置网络层实现(客户端)
     * @param rpcProperties
     * @return
     */
    @Bean
    public RpcRequestTransport rpcRequestTransport(RpcProperties rpcProperties) {
        // 设置服务发现
        ServiceDiscovery serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension(rpcProperties.getRegistryProtocol());
        // 设置负载均衡
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension(rpcProperties.getLoadBalance());
        //        return ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
        // 设置编码和压缩
        return new NettyRpcClient(serviceDiscovery, loadBalance, rpcProperties.getSerialization(), rpcProperties.getCompress());
    }

}
