package cn.krone.rpc.registry.config;

import cn.krone.rpc.common.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author xzq
 * @create 2022-06-12-23:40
 */
public final class Config {

    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    public static String getZookeeperAddress() {
        Properties properties;
        try (InputStream in = Config.class.getResourceAsStream("/registry.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        String zookeeperAddress = properties.getProperty("zookeeper.address");
        if (StringUtil.isBlank(zookeeperAddress)) {
            return DEFAULT_ZOOKEEPER_ADDRESS;
        } else {
            return zookeeperAddress;
        }
    }
}
