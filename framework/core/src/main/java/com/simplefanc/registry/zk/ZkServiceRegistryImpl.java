package com.simplefanc.registry.zk;

import com.simplefanc.registry.ServiceRegistry;
import com.simplefanc.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * @author chenfan
 * @date 2022/10/18 16:43
 **/
public class ZkServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        // 根节点下注册子节点：服务
        // /my-rpc/com.simplefanc.HelloServicegroup1version1/127.0.0.1:9998
        // inetSocketAddress.toString()：/127.0.0.1:9998
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }
}
