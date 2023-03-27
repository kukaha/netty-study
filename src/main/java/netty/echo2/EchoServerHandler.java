package netty.echo2;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * ServerHandler 用来实现 echo（回声，即原样返回 EchoClient 发来的任何消息）
 */

@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当通道有数据可读时执行
     *
     * @param ctx 上下文对象，可以从中取得相关联的 Pipeline、Channel 等
     * @param msg 客户端发送的数据
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 原样写回 EchoClient 发来的任何消息
        ctx.write(msg);
    }

    /**
     * 上面 channelRead()执行完成后，触发本函数的执行
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 出现异常的时候，关闭当前 SocketChannel
        cause.printStackTrace();
        ctx.close();
    }

}
