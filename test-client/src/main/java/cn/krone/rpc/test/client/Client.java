package cn.krone.rpc.test.client;

import cn.krone.rpc.api.AddService;
import cn.krone.rpc.api.MulService;
import cn.krone.rpc.consumer.RpcProxyFactory;
import cn.krone.rpc.consumer.RpcProxyFactoryJdkImpl;

/**
 * @author xzq
 * @create 2022-05-31-23:24
 */
public class Client {
    public static void main(String[] args) {
        RpcProxyFactoryJdkImpl rpcClientProxy = new RpcProxyFactoryJdkImpl("127.0.0.1", 9000);
        AddService addService = rpcClientProxy.getProxy(AddService.class);
////        MulService mulService = rpcClientProxy.getProxy(MulService.class);
        int addRes = addService.add(1, 2);
        int addRes2 = addService.add(2, 2);
////        int mulRes = mulService.multiply(1, 2);
        System.out.println("addRes : " + addRes);
        System.out.println("addRes2 : " + addRes2);
    }
}
