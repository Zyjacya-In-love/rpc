package cn.krone.rpc.loadbalance;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author xzq
 * @create 2022-06-15-22:12
 */
@Slf4j
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public InetSocketAddress select(List<String> serviceUrlList) {
        if (serviceUrlList == null || serviceUrlList.isEmpty()) {
            return null;
        }
        if (serviceUrlList.size() == 1) {
            return getInetSocketAddressByString(serviceUrlList.get(0));
        }
        return getInetSocketAddressByString(doSelect(serviceUrlList));
    }

    InetSocketAddress getInetSocketAddressByString(String targetServiceUrl) {
        log.info("Successfully found the service address:[{}]", targetServiceUrl);
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }

    protected abstract String doSelect(List<String> serviceAddressList);
}
