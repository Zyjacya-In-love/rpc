package cn.krone.rpc.test.client;

import cn.krone.rpc.api.AddService;
import cn.krone.rpc.consumer.RpcClientProxy;

/**
 * @author xzq
 * @create 2022-05-31-23:24
 */
public class Client {
    public static void main(String[] args) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy("127.0.0.1", 9000);
        AddService addService = rpcClientProxy.getProxy(AddService.class);
        int addRes = addService.add(1, 2);
        System.out.println("addRes : " + addRes);
    }
}
