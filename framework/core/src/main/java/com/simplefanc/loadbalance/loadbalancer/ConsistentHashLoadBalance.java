package com.simplefanc.loadbalance.loadbalancer;

import com.simplefanc.loadbalance.AbstractLoadBalance;
import com.simplefanc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author chenfan
 * @date 2022/10/18 16:46
 **/
public class ConsistentHashLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        return null;
    }
}
