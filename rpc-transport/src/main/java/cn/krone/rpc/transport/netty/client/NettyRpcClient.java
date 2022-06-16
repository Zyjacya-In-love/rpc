package cn.krone.rpc.transport.netty.client;

import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import cn.krone.rpc.common.factory.SingletonFactory;
import cn.krone.rpc.transport.netty.protocol.ProtocolFrameDecoder;
import cn.krone.rpc.transport.netty.protocol.RpcMessageCodec;
import cn.krone.rpc.transport.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * @author xzq
 * @create 2022-06-08-17:13
 */
@Slf4j
public class NettyRpcClient implements RpcClient {

    @Override
    public RpcResponse sendRequestAndGetResponse(RpcRequest rpcRequest, InetSocketAddress inetSocketAddress) {
        // 1. 将消息对象送出去，给 netty 的下一个 handler 处理
        Channel channel = ChannelPool.getChannel(inetSocketAddress);
        channel.writeAndFlush(rpcRequest);
        // 2. 准备一个空 Promise 对象，来接收结果             指定 promise 对象异步接收结果线程
        DefaultPromise<RpcResponse> promise = new DefaultPromise<>(channel.eventLoop());
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

}
