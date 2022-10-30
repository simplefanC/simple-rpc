package com.simplefanc.autoconfigure;

import com.simplefanc.autoconfigure.annotation.RpcService;
import com.simplefanc.remoting.transport.server.NettyRpcServer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.Objects;

/**
 * 服务启动暴露，自动注入Service
 *
 * @author chenfan
 * @date 2022/10/18 18:49
 **/
public class DefaultRpcProcessor implements ApplicationListener<ContextRefreshedEvent> {
    private final NettyRpcServer rpcServer;

    public DefaultRpcProcessor(NettyRpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Spring启动完毕过后会收到一个事件通知
        if (Objects.isNull(event.getApplicationContext().getParent())) {
            ApplicationContext context = event.getApplicationContext();
            Map<String, Object> beans = context.getBeansWithAnnotation(RpcService.class);
            if (beans.size() > 0) {
                rpcServer.start();
            }
        }
    }
}
