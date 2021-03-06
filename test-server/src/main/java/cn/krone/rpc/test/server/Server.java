package cn.krone.rpc.test.server;

import cn.krone.rpc.api.AddService;
import cn.krone.rpc.common.factory.SingletonFactory;
import cn.krone.rpc.transport.RpcServer;
import cn.krone.rpc.provider.ServiceProvider;
import cn.krone.rpc.provider.ServiceProviderImpl;
import cn.krone.rpc.transport.netty.server.NettyRpcServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author xzq
 * @create 2022-05-31-23:18
 */
public class Server {
    public static void main(String[] args) {
        String host = null;
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int port = 9000;
        AddService addService = new AddServiceImpl();
//        ServiceProvider serviceProvider = new ServiceProviderImpl();
        ServiceProvider serviceProvider = SingletonFactory.getInstance(
                                                                        ServiceProviderImpl.class,
                                                                        new Class[]{String.class, int.class},
                                                                        new Object[]{host, port});
        serviceProvider.register(addService);
        RpcServer rpcServer = new NettyRpcServer();
        rpcServer.start(port);
    }
}
