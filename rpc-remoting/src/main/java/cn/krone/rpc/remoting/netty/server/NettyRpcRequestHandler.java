package cn.krone.rpc.remoting.netty.server;

import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import cn.krone.rpc.common.factory.SingletonFactory;
import cn.krone.rpc.provider.RpcRequestHandler;
import cn.krone.rpc.provider.ServiceProvider;
import cn.krone.rpc.provider.ServiceProviderImpl;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xzq
 * @create 2022-06-08-14:59
 */
@Slf4j
@ChannelHandler.Sharable  // 错一次：加上 Sharable 才能只创建一个对象，让多个channel公用同一个
public class NettyRpcRequestHandler extends SimpleChannelInboundHandler<RpcRequest> {


//    private final RpcRequestHandler rpcRequestHandler;
//
//    public NettyRpcRequestHandler() {
//        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) throws Exception {
//        log.debug("NettyRpcRequestHandler channelRead0 ");
//        System.out.println("NettyRpcRequestHandler channelRead0 ");
//        RpcRequestHandler rpcRequestHandler = new RpcRequestHandler();
        RpcResponse response = null;
        try {
            // 调用专门的 rpc Request 处理对象
            Object returnObject = RpcRequestHandler.handle(rpcRequest);
            response = RpcResponse.success(returnObject);
        } catch (Exception e) {
            log.error("RpcRequestHandlerThread run exception：", e);
            response = RpcResponse.fail(RpcResponse.RpcResponseCodeEnum.FAIL);
            response.setMessage(e.getCause().getMessage());
        }
        response.setSequenceId(rpcRequest.getSequenceId());
        ctx.writeAndFlush(response);
    }
}
