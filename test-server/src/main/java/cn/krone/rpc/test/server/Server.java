package cn.krone.rpc.test.server;

import cn.krone.rpc.api.AddService;
import cn.krone.rpc.provider.RpcServer;
import cn.krone.rpc.provider.ServiceProvider;
import cn.krone.rpc.provider.ServiceProviderImpl;

/**
 * @author xzq
 * @create 2022-05-31-23:18
 */
public class Server {
    public static void main(String[] args) {
        AddService addService = new AddServiceImpl();
        ServiceProvider serviceProvider = new ServiceProviderImpl();
        serviceProvider.register(addService);
        RpcServer rpcServer = new RpcServer(serviceProvider);
        rpcServer.start(9000);
    }
}
