package com.simplefanc.autoconfigure;

import com.simplefanc.extension.ExtensionLoader;
import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.registry.ServiceDiscovery;
import com.simplefanc.registry.zk.ZkServiceDiscoveryImpl;
import com.simplefanc.remoting.transport.client.NettyRpcClient;
import com.simplefanc.remoting.transport.client.RpcRequestTransport;
import com.simplefanc.remoting.transport.server.NettyRpcServer;
import com.simplefanc.remoting.transport.server.handler.RpcRequestHandler;
import com.simplefanc.provider.ServiceProvider;
import com.simplefanc.provider.impl.ZkServiceProviderImpl;
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
    public SpringBeanPostProcessor beanPostProcessor(ServiceProvider serviceProvider, RpcRequestTransport rpcClient) {
        return new SpringBeanPostProcessor(serviceProvider, rpcClient);
    }

    @Bean
    public NettyRpcServer rpcServer(RpcProperties rpcProperties, ServiceProvider serviceProvider) {
        RpcRequestHandler requestHandler = new RpcRequestHandler(serviceProvider);
        return new NettyRpcServer(rpcProperties.getServerPort(), requestHandler);
    }

    // 服务发布
    @Bean
    public ServiceProvider serviceProvider(){
        // 可根据 RpcProperties 配置
        return new ZkServiceProviderImpl();
    }

    // 设置网络层实现
    @Bean
    public RpcRequestTransport rpcRequestTransport(RpcProperties rpcProperties) {
        // 设置负载均衡
        LoadBalance loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
        // 设置服务发现
//        ServiceDiscovery serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
        ServiceDiscovery serviceDiscovery = new ZkServiceDiscoveryImpl(loadBalance);

        // 设置编码和压缩
        return new NettyRpcClient(serviceDiscovery, rpcProperties.getCompress(), rpcProperties.getCodec());
//        return ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

}
