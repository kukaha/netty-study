package netty.echo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 一旦建立连接就会唤醒此方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server channelActive:" + "Thread:" + Thread.currentThread());
    }

    /**
     * 当收到客户端发过来的数据时此方法会被唤醒
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive client : [" + msg + "]");

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常就关闭
        cause.printStackTrace();
        ctx.close();
    }


}
