package com.simplefanc.remoting.transport.server;

import com.simplefanc.config.CustomShutdownHook;
import com.simplefanc.remoting.transport.codec.RpcMessageDecoder;
import com.simplefanc.remoting.transport.codec.RpcMessageEncoder;
import com.simplefanc.remoting.transport.server.handler.NettyRpcServerHandler;
import com.simplefanc.remoting.transport.server.handler.RpcRequestHandler;
import com.simplefanc.utils.RuntimeUtil;
import com.simplefanc.utils.threadpool.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author chenfan
 * @date 2022/10/18 16:32
 **/
@Slf4j
@Getter
public class NettyRpcServer {
    // TODO 能否也配置化
//    public static final int PORT = 9999;
    public final int serverPort;
    private final RpcRequestHandler requestHandler;
    private final String serialization;
    private final String compress;

    public NettyRpcServer(int serverPort, RpcRequestHandler requestHandler, String serialization, String compress) {
        this.serverPort = serverPort;
        this.requestHandler = requestHandler;
        this.serialization = serialization;
        this.compress = compress;
    }

    @SneakyThrows
    public void start() {
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        // 监听端口，accept 新连接的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        // 处理每一条连接的数据读写的线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 开启 2 倍的 cpu 核数个 NIO 线程
        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtil.cpus() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("service-handler-group", false)
        );
        try {
            // 引导我们进行服务端的启动工作
            ServerBootstrap b = new ServerBootstrap();
            // 给引导类配置两大线程组
            b.group(bossGroup, workerGroup)
                    // 指定 IO 模型
                    .channel(NioServerSocketChannel.class)
                    // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                    // 通俗地说，如果要求高实时性，有数据发送时就马上发送，就设置为 true 关闭，如果需要减少发送次数减少网络交互，就设置为 false 开启
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 当客户端第一次进行请求的时候才会进行初始化 // 定义后续每条连接的数据读写，业务处理逻辑
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 返回的是和这条连接相关的逻辑处理链，采用了责任链模式
                            ChannelPipeline p = ch.pipeline();
                            // 心跳与空闲检测: 30 秒之内没有收到客户端请求的话（就表示连接假死）就关闭连接
                            p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            p.addLast(new RpcMessageEncoder());
                            p.addLast(new RpcMessageDecoder());
                            p.addLast(serviceHandlerGroup, new NettyRpcServerHandler(requestHandler, serialization, compress));
                        }
                    });
            String host = InetAddress.getLocalHost().getHostAddress();
            // 绑定端口，同步等待绑定成功
            ChannelFuture f = b.bind(host, serverPort).sync();
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            serviceHandlerGroup.shutdownGracefully();
        }
    }
}
