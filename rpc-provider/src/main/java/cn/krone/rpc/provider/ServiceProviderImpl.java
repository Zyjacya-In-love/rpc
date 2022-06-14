package cn.krone.rpc.provider;

import cn.krone.rpc.common.exception.RpcErrorEnum;
import cn.krone.rpc.common.exception.RpcException;
import cn.krone.rpc.common.extension.ExtensionLoader;
import cn.krone.rpc.provider.config.Config;
import cn.krone.rpc.registry.ServiceRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 注册 和 获得 服务 的 接口实现类
 * 在这里 服务的名字 用的就是 对象实现的接口的名字的全类名
 * @author xzq
 * @create 2022-06-01-20:11
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    private final ConcurrentHashMap<String, Object> interfaceName2ServiceObjMap = new ConcurrentHashMap<>();
    private final Set<String> registeredServiceName = ConcurrentHashMap.newKeySet();

    private final String host;
    private final int port;

    public ServiceProviderImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void register(Object service) throws RpcException {
        String serviceName = service.getClass().getCanonicalName(); // 更容易理解的全类名
        if (registeredServiceName.contains(serviceName)) {
            return;
        }
        registeredServiceName.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0) {
            throw new RpcException(RpcErrorEnum.SERVICE_NOT_IMPLEMENT_INTERFACE);
        }
        // 注册中心
        String providerRegistryName = Config.getProviderRegistry();
        ServiceRegistry serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class)
                .getExtension(providerRegistryName);
        for (Class<?> oneInterface : interfaces) {
            String interfaceName = oneInterface.getCanonicalName();
            interfaceName2ServiceObjMap.put(interfaceName, service);
            serviceRegistry.registerService(interfaceName, new InetSocketAddress(host, port));
        }
        log.info("interfaces : {} register service : {}", interfaces, serviceName);

    }

    @Override
    public Object getService(String serviceInterfaceName) throws RpcException {
        if (!interfaceName2ServiceObjMap.containsKey(serviceInterfaceName)) {
            throw new RpcException(RpcErrorEnum.SERVICE_NOT_FOUND);
        }
        return interfaceName2ServiceObjMap.get(serviceInterfaceName);
    }
}
