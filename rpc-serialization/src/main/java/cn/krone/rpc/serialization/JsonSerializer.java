package cn.krone.rpc.serialization;


import cn.krone.rpc.common.exception.RpcErrorEnum;
import cn.krone.rpc.common.exception.RpcException;
import cn.krone.rpc.common.exchange.RpcRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * from CSDN PANDA : https://blog.csdn.net/qq_38685503/article/details/114290623
 * 在某个类的属性反序列化时，如果属性声明为 Object 的，就会造成反序列化出错，通常会把 Object 属性直接反序列化成 String 类型，就需要其他参数辅助序列化
 * 由于使用JSON序列化和反序列化Object数组，无法保证反序列化后仍然为原实例类，因此需要重新判断处理
 * @author xzq
 * @create 2022-06-09-21:10
 */
@Slf4j
public class JsonSerializer implements Serializer {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(Object obj) {
        try{
            return objectMapper.writeValueAsBytes(obj);
        }catch (JsonProcessingException e){
            log.error("SERIALIZE_ERROR : {}", e.getMessage());
            throw new RpcException(RpcErrorEnum.SERIALIZE_ERROR);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try{
            return objectMapper.readValue(bytes, clazz);
        }catch (IOException e){
            log.error("DESERIALIZE_ERROR : {}", e.getMessage());
            throw new RpcException(RpcErrorEnum.DESERIALIZE_ERROR);
        }
    }
}

