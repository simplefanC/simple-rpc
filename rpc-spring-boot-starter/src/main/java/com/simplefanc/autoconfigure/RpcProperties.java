package com.simplefanc.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author chenfan
 * @date 2022/10/18 18:23
 **/
@Data
@ConfigurationProperties(prefix = RpcProperties.RPC_PREFIX)
public class RpcProperties {
    public static final String RPC_PREFIX = "rpc";
    /**
     * 服务注册中心地址
     */
    private String registryAddress = "127.0.0.1:2181";

    private String registryProtocol = "zookeeper";

    /**
     * 服务暴露端口
     */
    private int serverPort = 9999;

    /**
     * 负载均衡算法
     */
    private String loadBalance = "random";

    /**
     * 权重，默认为1
     */
    private Integer weight = 1;

    private String compress = "gzip";

    private String serialization = "kyro";
}
