package netty.privateprotocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {

    private final MarshallingEncoder marshallingEncoder = MarshallingCodeCFactory.buildMarshallingEncoder();

    @Override
    protected void encode(ChannelHandlerContext ctx
            , NettyMessage nettyMessage
            , List<Object> out) throws Exception {
        if (nettyMessage == null || nettyMessage.getHeader() == null) {
            throw new Exception("编码消息为空");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt(nettyMessage.getHeader().getCrcCode());
        sendBuf.writeInt(nettyMessage.getHeader().getLength());
        sendBuf.writeLong(nettyMessage.getHeader().getSessionID());
        sendBuf.writeByte(nettyMessage.getHeader().getType());
        sendBuf.writeByte(nettyMessage.getHeader().getPriority());
        sendBuf.writeInt(nettyMessage.getHeader().getAttachment().size());

        // 对Attachment编码
        String key;
        byte[] keyArray;
        Object value;
        for (Map.Entry<String, Object> param : nettyMessage.getHeader().getAttachment().entrySet()) {
            key = param.getKey();
            keyArray = key.getBytes(StandardCharsets.UTF_8);
            sendBuf.writeInt(keyArray.length);
            sendBuf.writeBytes(keyArray);
            value = param.getValue();
            marshallingEncoder.encode(ctx, value, sendBuf);
        }

        if (nettyMessage.getBody() != null) {
            marshallingEncoder.encode(ctx, nettyMessage.getBody(), sendBuf);
        } else {
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(4, sendBuf.readableBytes());
        out.add(sendBuf);
    }

}
