package com.simplefanc.remoting.transport.client;


import com.simplefanc.extension.SPI;
import com.simplefanc.remoting.dto.RpcRequest;

/**
 * 发送 RPC 请求的顶层接口：可以有多种实现 如Netty socket等
 */
@SPI
public interface RpcRequestTransport {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
