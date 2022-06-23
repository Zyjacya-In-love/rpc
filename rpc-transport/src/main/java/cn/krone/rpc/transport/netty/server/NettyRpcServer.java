package cn.krone.rpc.transport.netty.server;

import cn.krone.rpc.transport.netty.protocol.ProtocolFrameDecoder;
import cn.krone.rpc.transport.netty.protocol.RpcMessageCodec;
import cn.krone.rpc.transport.RpcServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;


/**
 * @author xzq
 * @create 2022-06-08-14:09
 */
@Slf4j
public class NettyRpcServer implements RpcServer {
    @Override
    public void start(int port) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        RpcMessageCodec RPC_MESSAGE_CODEC = new RpcMessageCodec();
        NettyRpcRequestHandler RPC_HANDLER = new NettyRpcRequestHandler();
        DefaultEventExecutorGroup rpcRequestHandlerGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2
        );
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.channel(NioServerSocketChannel.class);
            // 全连接队列：表示系统用于临时存放已完成三次握手的请求的队列的最大长度,如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
            // TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
            serverBootstrap.childOption(ChannelOption.TCP_NODELAY, true); // 不用Nagle
            serverBootstrap.group(boss, worker);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
//                    log.debug("initChannel start");
                    // 记录了多次消息之间的状态就不对了，线程不安全的，不能多个 EventLoop 使用同一个 LengthFieldBasedFrameDecoder
                    ch.pipeline().addLast(new ProtocolFrameDecoder()); // 处理 粘包、半包 的 Decoder，放最前面

                    ch.pipeline().addLast(LOGGING_HANDLER);
//                    log.debug("after LOGGING_HANDLER");
                    ch.pipeline().addLast(RPC_MESSAGE_CODEC);
//                    log.debug("after RPC_MESSAGE_CODEC");
                    // 使用 group 执行 RPC_HANDLER 的处理，不再使用 nio 线程执行 Handler，
                    // 尽量将耗时的业务逻辑处理放入单独的业务线程池中处理，避免影响其他连接的IO读写，从而近一步影响整个服务程序的IO吞吐。
                    ch.pipeline().addLast(rpcRequestHandlerGroup, RPC_HANDLER);
//                    log.debug("initChannel finish");
                }
            });
            Channel channel = serverBootstrap.bind(port).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("server error", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            rpcRequestHandlerGroup.shutdownGracefully();
        }
    }
}
