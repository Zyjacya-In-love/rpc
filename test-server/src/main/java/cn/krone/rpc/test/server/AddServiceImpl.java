package cn.krone.rpc.test.server;

import cn.krone.rpc.api.AddService;

/**
 * @author xzq
 * @create 2022-05-31-23:22
 */
public class AddServiceImpl implements AddService {
    @Override
    public int add(int a, int b) {
        return a+b;
    }
}
