package netty.privateprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {

    private final MarshallingDecoder nettyMessageDecoder = MarshallingCodeCFactory.buildMarshallingDecoder();

    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        super.decode(ctx, in);
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setCrcCode(in.readInt());
        header.setLength(in.readInt());
        header.setSessionID(in.readLong());
        header.setType(in.readByte());
        header.setPriority(in.readByte());

        // 查看是否有 用于扩展消息头信息
        int size = in.readInt();
        if (size > 0) {
            Map<String, Object> attach = new HashMap<>(size);
            int keySize = 0;
            byte[] keyArray;
            String key;
            for (int i = 0; i < size; i++) {
                keySize = in.readInt();
                keyArray = new byte[keySize];
                in.readBytes(keyArray);
                key = new String(keyArray, StandardCharsets.UTF_8);
                attach.put(key, nettyMessageDecoder.decode(ctx, in));
            }
            header.setAttachment(attach);
        }
        if (in.readableBytes() > 4) {
            message.setBody(nettyMessageDecoder.decode(ctx, in));
        }
        message.setHeader(header);
        return message;
    }
}
