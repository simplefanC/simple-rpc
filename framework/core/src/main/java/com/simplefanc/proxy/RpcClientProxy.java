package com.simplefanc.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 当我们去调用一个远程的方法的时候，实际上是通过代理对象调用的。
 *
 * @author chenfan
 * @date 2022/10/18 16:37
 **/
public class RpcClientProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return null;
    }
}
