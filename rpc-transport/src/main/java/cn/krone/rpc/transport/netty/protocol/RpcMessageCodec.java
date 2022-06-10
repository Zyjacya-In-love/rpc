package cn.krone.rpc.transport.netty.protocol;

import cn.krone.rpc.common.exchange.RpcMessage;
import cn.krone.rpc.common.exchange.RpcRequest;
import cn.krone.rpc.common.exchange.RpcResponse;
import cn.krone.rpc.common.extension.ExtensionLoader;
import cn.krone.rpc.serialization.SerializationAlgorithmEnum;
import cn.krone.rpc.serialization.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * modify 黑马 netty
 * @author xzq
 * @create 2022-06-08-14:36
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcMessageCodec extends MessageToMessageCodec<ByteBuf, RpcMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(ProtocolConstants.MAGIC_NUMBER);
        // 2. 1 字节的版本
        out.writeByte(ProtocolConstants.VERSION);
        // 3. 1 字节的消息类型
        out.writeByte(msg.getMessageType());
        // 4. 1 字节的序列化方式
        byte serializerAlgorithmCode = msg.getSerializationAlgorithm();
        out.writeByte(serializerAlgorithmCode);
        // 5. 1 字节的压缩类型
        out.writeByte(msg.getCompressionAlgorithm());

        // 序列化 RpcMessage
        String serializationAlgorithmName = SerializationAlgorithmEnum.getNameByCode(serializerAlgorithmCode);
        log.info("serialization algorithm name: [{}] ", serializationAlgorithmName);
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                .getExtension(serializationAlgorithmName);
//        log.debug("serializer : {}", serializer.getClass());
        byte[] bytes = serializer.serialize(msg);

        // 6. 4 个字节的消息长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);

        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outList) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte messageType = in.readByte();
        byte serializationAlgorithm = in.readByte();
        byte compressAlgorithm = in.readByte();

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        // 反序列化
        String serializationAlgorithmName = SerializationAlgorithmEnum.getNameByCode(serializationAlgorithm);
        log.info("deserialization algorithm name: [{}] ", serializationAlgorithmName);
        Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                .getExtension(serializationAlgorithmName);
        System.out.println("serializer : " + serializer.getClass());
//        RpcMessage message = null;
//        if (messageType == RpcMessage.RPC_MESSAGE_TYPE_REQUEST) {
//            message = serializer.deserialize(bytes, RpcRequest.class);
//        } else if (messageType == RpcMessage.RPC_MESSAGE_TYPE_RESPONSE){
//            message = serializer.deserialize(bytes, RpcResponse.class);
//        }
        // 通用性++，相比一个一个 if 判断，用一个 map 把消息的 class 存起来，以后增添消息就只需要关注 RpcMessage 这个类不出错就可以了
        Class<? extends RpcMessage> messageClass = RpcMessage.getMessageClass(messageType);
        RpcMessage message = serializer.deserialize(bytes, messageClass);

        System.out.println("message : " + message);
//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        ;
        // 找到反序列化算法
//        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 确定具体消息类型
//        Class<? extends RpcMessage> messageClass = RpcMessage.getMessageClass(messageType);
//        RpcMessage message = (RpcMessage) ois.readObject();
//        ois.close();
//        Message message = algorithm.deserialize(messageClass, bytes);
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
//        log.debug("decode message : {}", message);
//        System.out.println("decode message : {}" + message);
        outList.add(message);
    }
}
