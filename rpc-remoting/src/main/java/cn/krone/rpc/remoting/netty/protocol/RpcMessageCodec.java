package cn.krone.rpc.remoting.netty.protocol;

import cn.krone.rpc.common.exchange.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        out.writeBytes(new byte[]{(byte)'r', (byte)'p', (byte)'c', (byte)'n'});
        // 2. 1 字节的版本,
        out.writeByte(1);
        // 3. 1 字节的消息类型
        out.writeByte(msg.getMessageType());
        // 4. 1 字节的序列化方式
        out.writeByte(0x00);
        // 5. 1 字节的压缩类型
        out.writeByte(0xff);

        // 获取内容的字节数组
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] bytes = bos.toByteArray();
        oos.close();
        bos.close();
        // 7. 4 个字节的消息长度
        out.writeInt(bytes.length);
        // 写入内容
        out.writeBytes(bytes);

//        System.out.println("encode : " + msg);
//        log.info("encode : {}" , msg);

        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> outList) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte messageType = in.readByte(); // 0,1,2...
        byte serializerAlgorithm = in.readByte(); // 0 或 1
        byte compressType = in.readByte();

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        ;
        // 找到反序列化算法
//        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        // 确定具体消息类型
//        Class<? extends RpcMessage> messageClass = RpcMessage.getMessageClass(messageType);
        RpcMessage message = (RpcMessage) ois.readObject();
        ois.close();
//        Message message = algorithm.deserialize(messageClass, bytes);
//        log.debug("{}, {}, {}, {}, {}, {}", magicNum, version, serializerType, messageType, sequenceId, length);
//        log.debug("{}", message);
//        log.debug("decode message : {}", message);
//        System.out.println("decode message : {}" + message);
        outList.add(message);
    }
}
