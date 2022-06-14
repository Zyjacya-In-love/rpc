package cn.krone.rpc.registry;

import cn.krone.rpc.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 通用 服务注册中心 接口，所有注册中心需实现两个方法，
 * registerService 服务注册，讲 服务名 和对应的 服务地址 写到注册中心
 * lookupService 服务发现，根据 服务名 查找 服务地址
 * @author xzq
 * @create 2022-06-12-23:16
 */
@SPI
public interface ServiceRegistry {

    void registerService(String serviceName, InetSocketAddress inetSocketAddress);

    InetSocketAddress lookupService(String serviceName);

}
