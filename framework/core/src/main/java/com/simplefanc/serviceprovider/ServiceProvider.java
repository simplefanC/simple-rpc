package com.simplefanc.serviceprovider;

import com.simplefanc.config.RpcServiceConfig;

/**
 * store and provide service object.
 */
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
    void publishService(RpcServiceConfig rpcServiceConfig);

}
