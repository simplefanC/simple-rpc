package com.simplefanc.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("application.yml"),

    RPC_CONFIG_KEY("rpc"),

    RPC_CONFIG_REGISTRY_ADDRESS("registry-address");

    private final String propertyValue;

}
