package cn.krone.rpc.transport;

import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import cn.krone.rpc.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * rpc 网络通信 Client 端通用接口，Client 网络传输类实现这个接口，发送请求并获得返回响应
 * @author xzq
 * @create 2022-06-08-14:13
 */
@SPI
public interface RpcClient {
    /**
     *
     * @param rpcRequest request from client
     * @return response from server
     */
    RpcResponse sendRequestAndGetResponse(RpcRequest rpcRequest, InetSocketAddress inetSocketAddress);
}
