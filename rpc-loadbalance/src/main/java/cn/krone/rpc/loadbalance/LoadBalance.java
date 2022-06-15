package cn.krone.rpc.loadbalance;

import cn.krone.rpc.common.extension.SPI;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 负载均衡 接口，负载均衡做的唯一的事情就是从一个服务的集群列表里选择其中一个地址返回
 * @author xzq
 * @create 2022-06-15-22:08
 */
@SPI
public interface LoadBalance {
    InetSocketAddress select(List<String> serviceUrlList);
}
