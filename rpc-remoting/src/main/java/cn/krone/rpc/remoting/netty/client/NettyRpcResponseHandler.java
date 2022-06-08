package cn.krone.rpc.remoting.netty.client;

import cn.krone.rpc.common.exchange.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xzq
 * @create 2022-06-08-15:57
 */
@Slf4j
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
}
