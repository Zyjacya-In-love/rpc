package cn.krone.rpc.transport.netty.client;

import cn.krone.rpc.common.exchange.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xzq
 * @create 2022-06-08-15:57
 */
@Slf4j
@ChannelHandler.Sharable  // 又错一次：加上 Sharable 才能只创建一个对象，让多个channel公用同一个
public class NettyRpcResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse msg) throws Exception {
//        log.debug("NettyRpcResponseHandler channelRead0 {}", msg);
//        System.out.println("msg.getSequenceId() : " + msg.getSequenceId());
        Promise<RpcResponse> promise = ReceiveResponsePromises.remove(msg.getSequenceId());
//        System.out.println("promise : " + promise);
        if (promise != null) {
            int statusCode = msg.getStatusCode();
            if(statusCode == RpcResponse.RpcResponseCodeEnum.FAIL.getCode()) {
                promise.setFailure(new IllegalStateException(msg.getMessage()));
            } else {
                promise.setSuccess(msg);
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("client catch exception：", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
