package com.simplefanc.registry;

import com.simplefanc.extension.SPI;
import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现
 */
@SPI
public interface ServiceDiscovery {
    /**
     * lookup service by rpcServiceName
     *
     * @param rpcRequest rpc service pojo
     * @return service address
     */
    InetSocketAddress lookupService(RpcRequest rpcRequest, LoadBalance loadBalance);
}
