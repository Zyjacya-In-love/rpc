package cn.krone.rpc.consumer.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import cn.krone.rpc.common.utils.StringUtil;
import cn.krone.rpc.serialization.SerializationAlgorithmEnum;


public final class Config {
    static Properties properties;
    static {
        try (InputStream in = Config.class.getResourceAsStream("/consumer.properties")) {
            properties = new Properties();
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static byte getSerializationAlgorithmCode() {
        String name = properties.getProperty("serialization.algorithm");
        if(StringUtil.isBlank(name)) {
            return SerializationAlgorithmEnum.JDK.getCode();
        } else {
            return SerializationAlgorithmEnum.getCodeByName(name);
        }
    }
    public static String getRpcClientTransport() {
        String name = properties.getProperty("client.transport");
        if(StringUtil.isBlank(name)) {
            return "netty";
        } else {
            return name;
        }
    }
    public static String getLoadBalanceAlgorithmName() {
        String name = properties.getProperty("load.balance");
        if(StringUtil.isBlank(name)) {
            return "random";
        } else {
            return name;
        }
    }
}