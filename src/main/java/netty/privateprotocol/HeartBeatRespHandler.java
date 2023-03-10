package netty.privateprotocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;

        // 返回心跳应答消息
        if (message.getHeader() != null
                && message.getHeader().getType() == MessageType.HEARTBEAT_REQ.getCode()) {
            System.out.println("Receive client heart beat message : ---> " + message);
            NettyMessage heartHeat = buildHeatBeat();
            System.out.println("Send heart beat response message to client : ---> " + heartHeat);
            ctx.writeAndFlush(heartHeat);
        } else {
            ctx.fireChannelRead(msg);
        }

    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(MessageType.HEARTBEAT_RESP.getCode());
        message.setHeader(header);
        return message;
    }

}
