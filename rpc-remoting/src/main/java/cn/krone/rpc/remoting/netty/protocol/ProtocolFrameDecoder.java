package cn.krone.rpc.remoting.netty.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * modify 黑马 netty
 * @author xzq
 * @create 2022-06-08-14:31
 */
public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder() {
        this(1024, 8, 4, 0, 0);
    }

    public ProtocolFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}

