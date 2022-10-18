package com.simplefanc.remoting.dto;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] parameters;
    private Class<?>[] paramTypes;
    /**
     * 为后续不兼容升级提供可能
     */
    private String version;
    /**
     * 处理一个接口有多个类实现的情况
     */
    private String group;

    /**
     * 完整的服务名称（class name + group + version）
     */
    public String getRpcServiceName() {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }
}
