package cn.krone.rpc.consumer;

import cn.krone.rpc.common.RpcRequest;
import cn.krone.rpc.common.RpcResponse;
import lombok.extern.slf4j.Slf4j;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @author xzq
 * @create 2022-05-31-22:21
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private String host;
    private int port;

    public RpcClientProxy(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 建造者模式
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .paramTypes(method.getParameterTypes())
                .args(args)
                .build();
        // 目前只允许成功，没有处理 RpcResponse 是 fail 的情况
        return ((RpcResponse) sendRequest(rpcRequest, host, port)).getData();
    }

    // 网络传输：发送逻辑，把 rpcRequest 发送到指定 的服务提供 host:port，并将 发回来的 RpcResponse 返回
    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            log.error("sendRequest() catch exception : ", e);
            return null;
        }
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
