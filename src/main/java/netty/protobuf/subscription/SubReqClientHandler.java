package netty.protobuf.subscription;

import cn.hutool.core.util.RandomUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.protobuf.SubscribeReqProto;

import java.util.ArrayList;
import java.util.List;

public class SubReqClientHandler extends ChannelInboundHandlerAdapter {

    /**
     * 一旦建立连接就会唤醒此方法
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Server channelActive:" + "Thread:" + Thread.currentThread());
        for (int i = 0; i < 10; i++) {
            List<String> address = new ArrayList<>();
            address.add("天津");
            address.add("北京");
            SubscribeReqProto.SubscribeReq subscribeReq =
                    SubscribeReqProto.SubscribeReq
                            .newBuilder()
                            .setSubReqID(RandomUtil.randomInt(6))
                            .setUserName("董晓斌")
                            .setProductName("python书")
                            .addAllAddress(address)
                            .build();
            ctx.write(subscribeReq);
        }
        ctx.flush();
    }

    /**
     * 当收到客户端发过来的数据时此方法会被唤醒
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("Receive server response : [" + msg + "]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常就关闭
        cause.printStackTrace();
        ctx.close();
    }


}
