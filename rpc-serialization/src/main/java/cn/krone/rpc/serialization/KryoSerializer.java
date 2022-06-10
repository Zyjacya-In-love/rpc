package cn.krone.rpc.serialization;

/**
 * @author xzq
 * @create 2022-06-09-22:31
 */

import cn.krone.rpc.common.exception.RpcErrorEnum;
import cn.krone.rpc.common.exception.RpcException;
import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * from：CSDN 何人听我楚狂声 https://blog.csdn.net/qq_40856284/article/details/107759017
 * Kryo 是一个快速高效的 Java 对象序列化框架，主要特点是高性能、高效和易用。
 * 最重要的两个特点，一是基于字节的序列化，对空间利用率较高，在网络传输时可以减小体积；二是序列化时记录属性对象的类型信息，这样在反序列化时就不会出现像json的问题了
 * 代码 from：JavaGuide
 */
public class KryoSerializer implements Serializer {

    /**
     * Because Kryo is not thread safe. So, use ThreadLocal to store Kryo objects
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // Object->byte:将对象序列化为byte数组
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            throw new RpcException(RpcErrorEnum.SERIALIZE_ERROR);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            // byte->Object:从byte数组中反序列化出对对象
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(o);
        } catch (Exception e) {
            throw new RpcException(RpcErrorEnum.SERIALIZE_ERROR);
        }
    }
}
