package com.simplefanc.registry.zk;

import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.registry.ServiceDiscovery;
import com.simplefanc.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author chenfan
 * @date 2022/10/18 16:43
 **/
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {
    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl(LoadBalance loadBalance) {
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        return null;
    }
}
