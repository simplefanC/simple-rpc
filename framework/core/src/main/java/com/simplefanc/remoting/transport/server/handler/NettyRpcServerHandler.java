package com.simplefanc.remoting.transport.server.handler;

import com.simplefanc.enums.CompressTypeEnum;
import com.simplefanc.enums.RpcResponseCodeEnum;
import com.simplefanc.enums.SerializationTypeEnum;
import com.simplefanc.remoting.constants.RpcConstants;
import com.simplefanc.remoting.dto.RpcMessage;
import com.simplefanc.remoting.dto.RpcRequest;
import com.simplefanc.remoting.dto.RpcResponse;
import com.simplefanc.utils.threadpool.ThreadPoolFactoryUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

/**
 * @author chenfan
 * @date 2022/10/18 16:32
 **/
@Slf4j
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final RpcRequestHandler rpcRequestHandler;
    private final String serialization;
    private final String compress;

    private final ExecutorService pool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("request-handler-pool");

    public NettyRpcServerHandler(RpcRequestHandler requestHandler, String serialization, String compress) {
        this.rpcRequestHandler = requestHandler;
        this.compress = compress;
        this.serialization = serialization;
    }

    /**
     * 读取从客户端消息，然后调用目标服务的目标方法并返回给客户端。
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 不应该让事件处理逻辑在 IO 线程上执行，而是应该派发到线程池中去执行
        // 原因也很简单，IO 线程主要用于接收请求，如果 IO 线程被占满，将导致它不能接收新的请求。
        pool.submit(() -> {
            try {
                if (msg instanceof RpcMessage) {
                    log.info("server receive msg: [{}] ", msg);
                    byte messageType = ((RpcMessage) msg).getMessageType();
                    RpcMessage rpcMessage = new RpcMessage();
                    rpcMessage.setSerialization(SerializationTypeEnum.getCode(this.serialization));
                    rpcMessage.setCompress(CompressTypeEnum.getCode(this.compress));
                    if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
                        rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                        rpcMessage.setData(RpcConstants.PONG);
                    } else {
                        RpcRequest rpcRequest = (RpcRequest) ((RpcMessage) msg).getData();
                        // Execute the target method (the method the client needs to execute) and return the method result
                        // 不应该让事件处理逻辑在 IO 线程上执行，而是应该派发到线程池中去执行
                        // 原因也很简单，IO 线程主要用于接收请求，如果 IO 线程被占满，将导致它不能接收新的请求。
                        Object result = rpcRequestHandler.handle(rpcRequest);
                        log.info(String.format("server get result: %s", result.toString()));
                        rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
                        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                            RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                            rpcMessage.setData(rpcResponse);
                        } else {
                            RpcResponse<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                            rpcMessage.setData(rpcResponse);
                            log.error("not writable now, message dropped");
                        }
                    }
                    ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                }
            } finally {
                //Ensure that ByteBuf is released, otherwise there may be memory leaks
                ReferenceCountUtil.release(msg);
            }
        });
    }

    /**
     * Netty 心跳机制相关。保证客户端和服务端的连接不被断掉，避免重连。
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("server catch exception");
        cause.printStackTrace();
        ctx.close();
    }
}
