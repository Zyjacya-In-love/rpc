package cn.krone.rpc.provider.config;

import cn.krone.rpc.common.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author xzq
 * @create 2022-06-13-23:15
 */
public final class Config {
    public static String getProviderRegistry() {
        Properties properties;
        try (InputStream in = Config.class.getResourceAsStream("/provider.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        String name = properties.getProperty("provider.registry");
        if(StringUtil.isBlank(name)) {
            return "zookeeper";
        } else {
            return name;
        }
    }
}
