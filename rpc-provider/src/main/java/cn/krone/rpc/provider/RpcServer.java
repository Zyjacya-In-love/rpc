package cn.krone.rpc.provider;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @author xzq
 * @create 2022-05-31-22:54
 */
@Slf4j // 在方法中直接使用log
public class RpcServer {

    // 线程池，来一个 rpc 请求，新建一个线程处理
    private final ExecutorService threadPool;
    private final RpcRequestHandler rpcRequestHandler;
    private final ServiceProvider serviceProvider;

    public RpcServer(ServiceProvider serviceProvider) {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
        rpcRequestHandler = new RpcRequestHandler();
        this.serviceProvider = serviceProvider;
    }

    // 服务提供端启动提供服务
    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("server provider start...");
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                log.info("client consumer send request! IP : " + socket.getInetAddress());
                threadPool.execute(new RpcRequestHandlerThread(socket, rpcRequestHandler, serviceProvider));
            }
        } catch (IOException e) {
            log.error("RpcServer start exception : ", e);
        }
    }
}
