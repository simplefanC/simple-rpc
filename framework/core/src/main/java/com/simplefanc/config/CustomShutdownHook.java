package com.simplefanc.config;

import com.simplefanc.utils.threadpool.ThreadPoolFactoryUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * When the server is closed, do something such as unregister all services
 */
@Slf4j
public class CustomShutdownHook {
    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();

    public static CustomShutdownHook getCustomShutdownHook() {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public void clearAll() {
        log.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            // 改为临时节点 省去 clearRegistry 操作
//            try {
//                InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 9999);
//                CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
//            } catch (UnknownHostException ignored) {
//            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}