package cn.krone.rpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

/**
 * 随机负载均衡，在集合里随机选一个
 * @author xzq
 * @create 2022-06-15-22:17
 */
public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddressList) {
        return serviceAddressList.get(new Random().nextInt(serviceAddressList.size()));
    }
}
