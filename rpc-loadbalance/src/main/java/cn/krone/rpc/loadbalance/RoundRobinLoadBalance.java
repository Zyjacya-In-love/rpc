package cn.krone.rpc.loadbalance;

import java.util.List;

/**
 * @author xzq
 * @create 2022-06-15-23:36
 */
public class RoundRobinLoadBalance extends AbstractLoadBalance {

    private int index = 0;

    @Override
    protected String doSelect(List<String> serviceAddressList) {
        if(index >= serviceAddressList.size()){
            index %= serviceAddressList.size();
        }
        return serviceAddressList.get(index++);
    }
}
