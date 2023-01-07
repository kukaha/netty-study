package netty.protobuf.subscription;

import cn.hutool.core.util.RandomUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.protobuf.SubscribeReqProto;
import netty.protobuf.SubscribeRespProto;

import java.util.ArrayList;
import java.util.List;

public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

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
        SubscribeReqProto.SubscribeReq subscribeReq = (SubscribeReqProto.SubscribeReq) msg;
        if ("董晓斌".equals(subscribeReq.getUserName())) {
            System.out.println("service accept client subscribe req : [" + subscribeReq + "]");
            SubscribeRespProto.SubscribeResp subscribeReqResp =
                    SubscribeRespProto.SubscribeResp
                            .newBuilder()
                            .setSubRespID(subscribeReq.getSubReqID())
                            .setRespCode(200)
                            .setDesc("netty book order success, 3 day later, sent to the designated address")
                            .build();
            ctx.writeAndFlush(subscribeReqResp);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //发生异常就关闭
        cause.printStackTrace();
        ctx.close();
    }


}
