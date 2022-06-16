package cn.krone.rpc.transport.netty.client;

import cn.krone.rpc.transport.netty.protocol.ProtocolFrameDecoder;
import cn.krone.rpc.transport.netty.protocol.RpcMessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xzq
 * @create 2022-06-14-13:17
 */
@Slf4j
public class ChannelPool {
    private static final Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    private static final NioEventLoopGroup group = new NioEventLoopGroup();
    private static final Bootstrap bootstrap;

    static {
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        RpcMessageCodec MESSAGE_CODEC = new RpcMessageCodec();
        NettyRpcResponseHandler RPC_HANDLER = new NettyRpcResponseHandler();
        bootstrap = new Bootstrap();
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
    }

    public static Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // determine if there is a connection for the corresponding address
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // if so, determine if the connection is available, and if so, get it directly
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public static void put(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    //    private static Channel channel = null;
    private static final Object LOCK = new Object();

    // 获取唯一的 channel 对象，懒汉单例，双重检测锁🔒
    public static Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = get(inetSocketAddress);
        if (channel != null) {
            return channel;
        }
        synchronized (LOCK) { //  t2
            if (channel != null) { // t1
                return channel;
            }
            channel = doConnect(inetSocketAddress);
            put(inetSocketAddress, channel);
            return channel;
        }
    }

    // 连接
    private static Channel doConnect(InetSocketAddress inetSocketAddress) {
        Channel channel = null;
        try {
            channel = bootstrap.connect(inetSocketAddress).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client connect error : {}", e.toString());
        }
        return channel;
    }

}
