package com.simplefanc.loadbalance.loadbalancer;

import com.simplefanc.loadbalance.AbstractLoadBalance;
import com.simplefanc.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 随机
 *
 * @author chenfan
 * @date 2022/10/18 16:46
 **/
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
