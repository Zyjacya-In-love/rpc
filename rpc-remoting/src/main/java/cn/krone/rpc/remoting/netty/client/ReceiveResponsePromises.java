package cn.krone.rpc.remoting.netty.client;

import cn.krone.rpc.common.exchange.RpcResponse;
import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参考 JavaGuide 和 黑马netty 实现
 * 因为发送之后是非阻塞的，所以发送后会立刻返回，而无法得到结果。
 * 利用 netty 的 Promise 实现两线程通信（nio线程和Client等待结果的主线程），
 * 通过一个 map 将序列号和 Promise 绑定，等结果返回的时候，Client的handler可以通过序列号获得当前请求对应的Promise，并将结果设置进对用的Promise中，
 * 这时 RpcProxyFactoryJdkImpl 中阻塞的 promise.await() 等到结果，当场结束阻塞继续运行，最终将结果返回给调用端
 * @author xzq
 * @create 2022-06-08-16:08
 */
public class ReceiveResponsePromises { // 接收响应的 Promise 们
    //                       序号      用来接收结果的 Promise 对象
    public static final Map<Integer, Promise<RpcResponse>> seqId2promise = new ConcurrentHashMap<>();

    public static void put(Integer seqId, Promise<RpcResponse> promise) {
        seqId2promise.put(seqId, promise);
    }

    public static Promise<RpcResponse> remove(Integer seqId) {
        return seqId2promise.remove(seqId);
    }

}
