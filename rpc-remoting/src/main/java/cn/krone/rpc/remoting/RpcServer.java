package cn.krone.rpc.remoting;

/**
 * rpc 网络通信 Server 端通用接口，用于启动服务端以提供服务
 * @author xzq
 * @create 2022-06-08-14:10
 */
public interface RpcServer {
    void start(int port);
}
