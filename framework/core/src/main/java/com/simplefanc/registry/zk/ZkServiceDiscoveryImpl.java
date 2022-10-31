package com.simplefanc.registry.zk;

import com.simplefanc.enums.RpcErrorMessageEnum;
import com.simplefanc.exception.RpcException;
import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.registry.ServiceDiscovery;
import com.simplefanc.registry.zk.util.CuratorUtils;
import com.simplefanc.remoting.dto.RpcRequest;
import com.simplefanc.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 根据完整的服务名称便可以将对应的服务地址查出来，查出来的服务地址可能并不止一个。故需要负载均衡。
 *
 * @author chenfan
 * @date 2022/10/18 16:43
 **/
@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    public ZkServiceDiscoveryImpl() {
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest, LoadBalance loadBalance) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // 负载均衡
        String targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList, rpcRequest);
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
