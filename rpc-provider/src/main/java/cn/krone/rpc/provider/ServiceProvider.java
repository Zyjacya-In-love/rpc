package cn.krone.rpc.provider;

/**
 * 注册 和 获得 服务 的 接口
 * @author xzq
 * @create 2022-06-01-19:31
 */
public interface ServiceProvider {
    void register(Object service);
    Object getService(String ServiceName);
}
