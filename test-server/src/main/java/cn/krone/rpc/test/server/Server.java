package cn.krone.rpc.test.server;

import cn.krone.rpc.api.AddService;
import cn.krone.rpc.provider.RpcServer;

/**
 * @author xzq
 * @create 2022-05-31-23:18
 */
public class Server {
    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer();
        AddService addService = new AddServiceImpl();
        rpcServer.start(addService, 9000);
    }
}
