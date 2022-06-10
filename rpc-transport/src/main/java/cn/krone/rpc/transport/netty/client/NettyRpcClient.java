package cn.krone.rpc.transport.netty.client;

import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import cn.krone.rpc.transport.netty.protocol.ProtocolFrameDecoder;
import cn.krone.rpc.transport.netty.protocol.RpcMessageCodec;
import cn.krone.rpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xzq
 * @create 2022-06-08-17:13
 */
@Slf4j
public class NettyRpcClient implements RpcClient {

    @Override
    public RpcResponse sendRequestAndGetResponse(RpcRequest rpcRequest) {
        // 1. 将消息对象送出去，给 netty 的下一个 handler 处理
        getChannel().writeAndFlush(rpcRequest);
        // 2. 准备一个空 Promise 对象，来接收结果             指定 promise 对象异步接收结果线程
        DefaultPromise<RpcResponse> promise = new DefaultPromise<>(getChannel().eventLoop());
        ReceiveResponsePromises.put(rpcRequest.getSequenceId(), promise);
        // 3. 等待 promise 结果
        try {
            promise.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(promise.isSuccess()) {
            // 调用正常
            return promise.getNow();
        } else {
            // 调用失败
            throw new RuntimeException(promise.cause());
        }
    }

    private static Channel channel = null;
    private static final Object LOCK = new Object();

    // 获取唯一的 channel 对象，懒汉单例，双重检测锁🔒
    public static Channel getChannel() {
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) { //  t2
            if (channel != null) { // t1
                return channel;
            }
            initChannel();
            return channel;
        }
    }

    // 初始化 channel 方法
    private static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        RpcMessageCodec MESSAGE_CODEC = new RpcMessageCodec();
        NettyRpcResponseHandler RPC_HANDLER = new NettyRpcResponseHandler();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(group);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(LOGGING_HANDLER);
                ch.pipeline().addLast(new ProtocolFrameDecoder());
                ch.pipeline().addLast(MESSAGE_CODEC);
                ch.pipeline().addLast(RPC_HANDLER);
            }
        });
        try {
            channel = bootstrap.connect("127.0.0.1", 9000).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
