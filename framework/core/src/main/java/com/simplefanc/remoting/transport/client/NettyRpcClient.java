package com.simplefanc.remoting.transport.client;

import com.simplefanc.enums.CompressTypeEnum;
import com.simplefanc.enums.SerializationTypeEnum;
import com.simplefanc.factory.SingletonFactory;
import com.simplefanc.loadbalance.LoadBalance;
import com.simplefanc.registry.ServiceDiscovery;
import com.simplefanc.remoting.constants.RpcConstants;
import com.simplefanc.remoting.dto.RpcMessage;
import com.simplefanc.remoting.dto.RpcRequest;
import com.simplefanc.remoting.dto.RpcResponse;
import com.simplefanc.remoting.transport.client.handler.NettyRpcClientHandler;
import com.simplefanc.remoting.transport.codec.RpcMessageDecoder;
import com.simplefanc.remoting.transport.codec.RpcMessageEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author chenfan
 * @date 2022/10/18 16:33
 **/
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {
    private final ServiceDiscovery serviceDiscovery;
    private final LoadBalance loadBalance;
    private final String compress;
    private final String serialization;

    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    public NettyRpcClient(ServiceDiscovery serviceDiscovery, LoadBalance loadBalance, String serialization, String compress) {
        this.serviceDiscovery = serviceDiscovery;
        this.loadBalance = loadBalance;
        this.compress = compress;
        this.serialization = serialization;

        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);

        // initialize resources such as EventLoopGroup, Bootstrap
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        // 1. ????????????
        bootstrap.group(eventLoopGroup)
                // 2. IO ??????
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // ???????????????????????????????????????????????????????????????????????????????????????
                // The timeout period of the connection.
                // If this time is exceeded or the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 3. ????????????????????????
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // ??????????????????????????????????????????????????????????????????????????????
                        ChannelPipeline p = ch.pipeline();
                        // ?????? 5 ??????????????????????????????????????????????????????????????????????????????????????????????????????
                        // If no data is sent to the server within 5 seconds, a heartbeat request is sent
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler(channelProvider, serialization, compress));
                    }
                });
    }

    /**
     * ???????????? rpc ??????(RpcRequest) ????????????
     *
     * @param rpcRequest message body
     * @return
     */
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // build return value
        CompletableFuture<RpcResponse<Object>> resultFuture = new CompletableFuture<>();
        // ????????????
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest, loadBalance);
        // ?????? Channel??????????????????
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            // put unprocessed request
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            RpcMessage rpcMessage = RpcMessage.builder().data(rpcRequest)
                    // ???????????????????????????
                    .serialization(SerializationTypeEnum.getCode(this.serialization))
                    .compress(CompressTypeEnum.getCode(this.compress))
                    .messageType(RpcConstants.REQUEST_TYPE).build();
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }

        return resultFuture;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????? Channel
     * connect server and get the channel ,so that you can send rpc message to server
     *
     * @param inetSocketAddress server address
     * @return the channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("The client has connected [{}] successful!", inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        return completableFuture.get();
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }
}
