package cn.krone.rpc.consumer;

import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import cn.krone.rpc.common.factory.SingletonFactory;
import cn.krone.rpc.common.utils.SequenceIdGenerator;
import cn.krone.rpc.remoting.RpcClient;
import cn.krone.rpc.remoting.netty.client.NettyRpcClient;
import lombok.extern.slf4j.Slf4j;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author xzq
 * @create 2022-05-31-22:21
 */
@Slf4j
public class RpcProxyFactoryJdkImpl implements RpcProxyFactory, InvocationHandler{

    private String host;
    private int port;
    private final RpcClient rpcClient = SingletonFactory.getInstance(NettyRpcClient.class);

    public RpcProxyFactoryJdkImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        // 1. 将方法调用转换为 消息对象
        // 建造者模式
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getCanonicalName()) // 更容易理解的全类名
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        int sequenceId = SequenceIdGenerator.nextId();
        rpcRequest.setSequenceId(sequenceId);
        // 2. 拆分逻辑，剩下的 交给 网络通信模块 去发送 去拿到结果
        RpcResponse rpcResponse = rpcClient.sendRequestAndGetResponse(rpcRequest);
        return rpcResponse.getData();
    }

    // 相当于获得代理类的工厂
    @SuppressWarnings("unchecked") // 忽略 unchecked 警告信息，在强制类型转换的时候编译器会给出警告
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),  // 目标类的类加载器
                new Class<?>[]{clazz},   // 代理需要实现的接口（可指定多个），传过来的就是一个 接口.class，用自己就行了
                this                  // 代理对象对应的自定义 InvocationHandler
        );
    }
}
