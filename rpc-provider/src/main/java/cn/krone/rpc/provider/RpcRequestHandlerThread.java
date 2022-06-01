package cn.krone.rpc.provider;

import cn.krone.rpc.common.RpcRequest;
import cn.krone.rpc.common.RpcResponse;
import cn.krone.rpc.common.exception.RpcException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author xzq
 * @create 2022-05-31-23:09
 */
@AllArgsConstructor
@Slf4j
public class RpcRequestHandlerThread implements Runnable {

    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;
    private final ServiceProvider serviceProvider;

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            // 获取 Service
            Object service = serviceProvider.getService(rpcRequest.getInterfaceName());
            // 调用专门的 rpc Request 处理对象
            Object returnObject = rpcRequestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(returnObject));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException | RpcException e) {
            log.error("RpcRequestHandlerThread run exception：", e);
        }
    }
}
