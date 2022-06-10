package cn.krone.rpc.transport.netty.protocol;

/**
 * @author xzq
 * @create 2022-06-09-14:04
 */
public class ProtocolConstants {

    // Protocol
    // 1. 4 字节的魔数
    public static final byte[] MAGIC_NUMBER = new byte[]{(byte)'r', (byte)'p', (byte)'c', (byte)'n'};
    // 2. 1 字节的版本
    public static final byte VERSION = 1;
    // 3. 1 字节的消息类型
    // 4. 1 字节的序列化方式
    // 5. 1 字节的压缩类型
    // 6. 4 个字节的消息长度

    // LengthFieldBasedFrameDecoder
    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
    public static final int LENGTH_FIELD_OFFSET = 8;
    public static final int LENGTH_FIELD_LENGTH = 4;
    public static final int LENGTH_ADJUSTMENT = 0;
    public static final int INITIAL_BYTES_TO_STRIP = 0;

//    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
//    public static final byte TOTAL_LENGTH = 16;
//    public static final byte REQUEST_TYPE = 1;
//    public static final byte RESPONSE_TYPE = 2;
//    //ping
//    public static final byte HEARTBEAT_REQUEST_TYPE = 3;
//    //pong
//    public static final byte HEARTBEAT_RESPONSE_TYPE = 4;
//    public static final int HEAD_LENGTH = 16;
//    public static final String PING = "ping";
//    public static final String PONG = "pong";
}
