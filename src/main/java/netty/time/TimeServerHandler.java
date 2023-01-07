package netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");

    int counter = 0;

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
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // QUERY TIME ORDER
        String query = (String) msg;

        System.out.println("Thread:" + Thread.currentThread() + "   receive request:" + query + "the counter is :" + ++counter);

        String time = "QUERY TIME ORDER".equalsIgnoreCase(query) ? format.format(new Date()) + "" : "BAD REQUEST";

        time = time + System.getProperty("line.separator");
        // 在netty中所有的信息都是封装在缓冲区中的
        ByteBuf result = Unpooled.copiedBuffer(time.getBytes());
        ctx.writeAndFlush(result);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常就关闭
        cause.printStackTrace();
        ctx.close();
    }

}
