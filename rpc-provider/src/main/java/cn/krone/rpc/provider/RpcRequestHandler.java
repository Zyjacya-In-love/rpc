package cn.krone.rpc.provider;

import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.factory.SingletonFactory;
import cn.krone.rpc.provider.ServiceProvider;
import cn.krone.rpc.provider.ServiceProviderImpl;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 拆分逻辑，专门处理 rpc 的 request 请求
 * @author xzq
 * @create 2022-06-01-20:18
 */
@Slf4j
public class RpcRequestHandler {
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
    }

//    private final ServiceProvider serviceProvider;
//
//    public RpcRequestHandler() {
//        this.serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
//    }

    public static Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getService(rpcRequest.getInterfaceName());

        Object returnObject = null;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            returnObject = method.invoke(service, rpcRequest.getArgs());
            log.info("{} invoke method : {}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return returnObject;
    }
}
