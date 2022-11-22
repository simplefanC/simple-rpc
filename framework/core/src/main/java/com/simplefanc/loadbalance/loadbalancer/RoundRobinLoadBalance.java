package com.simplefanc.loadbalance.loadbalancer;

import com.simplefanc.loadbalance.AbstractLoadBalance;
import com.simplefanc.remoting.dto.RpcRequest;

import java.util.List;

public class RoundRobinLoadBalance extends AbstractLoadBalance {
    private volatile int index;

    @Override
    protected synchronized String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        // 加锁防止多线程情况下，index超出services.size()
        if (index == serviceAddresses.size()) {
            index = 0;
        }
        return serviceAddresses.get(index++);
    }
}
