package cn.krone.rpc.consumer;

import java.lang.reflect.InvocationHandler;

/**
 * @author xzq
 * @create 2022-06-08-14:23
 */
public interface RpcProxyFactory {
    // 相当于获得代理类的工厂
    <T> T getProxy(Class<T> clazz);
}
