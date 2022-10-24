package com.simplefanc.remoting.transport.server.handler;

import com.simplefanc.exception.RpcException;
import com.simplefanc.remoting.dto.RpcRequest;
import com.simplefanc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 处理 RPC 请求
 *
 * @author chenfan
 * @date 2022/10/18 20:07
 **/
@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object handle(RpcRequest rpcRequest) {
        // 获取 target service
        Object service = serviceProvider.getService(rpcRequest.getRpcServiceName());
        // 调用
        return invokeTargetMethod(rpcRequest, service);
    }

    /**
     * 反射调用 目标方法
     * @param rpcRequest
     * @param service
     * @return
     */
    private Object invokeTargetMethod(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            // 反射调用 target service 的 目标方法
            result = method.invoke(service, rpcRequest.getParameters());
            log.info("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
