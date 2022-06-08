package cn.krone.rpc.test.server;

import cn.krone.rpc.api.AddService;
import cn.krone.rpc.common.factory.SingletonFactory;
import cn.krone.rpc.remoting.RpcServer;
import cn.krone.rpc.provider.ServiceProvider;
import cn.krone.rpc.provider.ServiceProviderImpl;
import cn.krone.rpc.remoting.netty.server.NettyRpcServer;

/**
 * @author xzq
 * @create 2022-05-31-23:18
 */
public class Server {
    public static void main(String[] args) {
        AddService addService = new AddServiceImpl();
//        ServiceProvider serviceProvider = new ServiceProviderImpl();
        ServiceProvider serviceProvider = SingletonFactory.getInstance(ServiceProviderImpl.class);
        serviceProvider.register(addService);
        RpcServer rpcServer = new NettyRpcServer();
        rpcServer.start(9000);
    }
}
