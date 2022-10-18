package com.simplefanc.proxy;

import com.simplefanc.config.RpcServiceConfig;
import com.simplefanc.remoting.transport.RpcRequestTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 当我们去调用一个远程的方法的时候，实际上是通过代理对象调用的。
 *
 * @author chenfan
 * @date 2022/10/18 16:37
 **/
public class RpcClientProxy implements InvocationHandler {
    public RpcClientProxy(RpcRequestTransport rpcClient, RpcServiceConfig rpcServiceConfig) {

    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
}
