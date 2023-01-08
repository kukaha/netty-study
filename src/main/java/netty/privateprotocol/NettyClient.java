package netty.privateprotocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {

    public static void main(String[] args) throws Exception {
        new NettyClient().connect(8888, "127.0.0.1");
    }

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    public void connect(int port, String host) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();//客户端的NIO的辅助启动类
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // 用来创建客户端的Channel
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                    ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
//                    ch.pipeline().addLast("ReadTimeoutHandler", new ReadTimeoutHandler(1000));
                    ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
//                    ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
                }
            });

            // 启动客户端
            ChannelFuture f = b.connect(host, port).sync(); // 客户端请求链接服务端

            // 等待连接关闭
            f.channel().closeFuture().sync();
        } finally {
            executor.execute(() -> {
                try {
                    // 发起重连
                    TimeUnit.SECONDS.sleep(5);
                    connect(8888, "127.0.0.1");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
//            workerGroup.shutdownGracefully();
        }
    }

}
