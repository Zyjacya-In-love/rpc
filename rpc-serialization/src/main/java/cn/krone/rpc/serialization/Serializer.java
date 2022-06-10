package cn.krone.rpc.serialization;

import cn.krone.rpc.common.extension.SPI;

/**
 * 序列化接口，所有序列化算法需要实现对应的 序列化和反序列化方法
 * @author xzq
 * @create 2022-06-09-14:28
 */
@SPI
public interface Serializer {
    byte[] serialize(Object obj);
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
