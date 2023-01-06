package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {


    public void channelActive(ChannelHandlerContext ctx) {
        for (int i = 0; i < 5; i++) {
            byte[] bytes = ("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
            ByteBuf buf = Unpooled.copiedBuffer(bytes);
            ctx.writeAndFlush(buf);
        }

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String result = (String) msg;
        System.out.println("now is:" + result);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}