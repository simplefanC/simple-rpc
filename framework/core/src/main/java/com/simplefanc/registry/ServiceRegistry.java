package com.simplefanc.registry;

import com.simplefanc.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 服务注册
 */
@SPI
public interface ServiceRegistry {
    /**
     * register service
     *
     * @param rpcServiceName    rpc service name
     * @param inetSocketAddress service address
     */
    void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress);

}
