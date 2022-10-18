package com.simplefanc.loadbalance;

import com.simplefanc.extension.SPI;
import com.simplefanc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * @author chenfan
 * @date 2022/10/18 16:45
 **/
@SPI
public interface LoadBalance {
    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
