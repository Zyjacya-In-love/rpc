package cn.krone.rpc.common.exchange;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * from 黑马 netty
 * @author yihang
 * @create 2022-06-08-14:47
 */

@Data
public abstract class RpcMessage implements Serializable {

    /**
     * 根据消息类型字节，获得对应的消息 class
     * @param messageType 消息类型字节
     * @return 消息 class
     */
    public static Class<? extends RpcMessage> getMessageClass(int messageType) {
        return messageClasses.get(messageType);
    }

    private int sequenceId;

    private byte messageType;

    private byte serializationAlgorithm;

    private byte compressionAlgorithm;

    public abstract byte getMessageType();

    /**
     * 请求类型 byte 值
     */
    public static final int RPC_MESSAGE_TYPE_REQUEST = 0;
    /**
     * 响应类型 byte 值
     */
    public static final int  RPC_MESSAGE_TYPE_RESPONSE = 1;

//    public static final int PING_MESSAGE = 2;
//    public static final int PONG_MESSAGE = 3;

    private static final Map<Integer, Class<? extends RpcMessage>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequest.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponse.class);
    }

}
