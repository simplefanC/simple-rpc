package com.simplefanc.provider;

import com.simplefanc.config.RpcServiceConfig;
import com.simplefanc.extension.SPI;
import com.simplefanc.registry.ServiceRegistry;

/**
 * 服务提供者
 * store and provide service object.
 */
@SPI
public interface ServiceProvider {

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc service name
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void publishService(ServiceRegistry serviceRegistry, RpcServiceConfig rpcServiceConfig);

}
