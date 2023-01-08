package netty.privateprotocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class NettyServer {


    public void bind() throws Exception {
        // NioEventLoopGroup线程组，用于接收客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于进行SocketChanel的网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();// Server端的NIO的辅助启动类
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// 对应JDK
                    .option(ChannelOption.SO_BACKLOG, 100) // 设置TCP一些参数
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    // NIO类库中的ServerSocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
//                            ch.pipeline().addLast(new NettyMessageEncoder());
//                            ch.pipeline().addLast("ReadTimeoutHandler", new ReadTimeoutHandler(1000));
//                            ch.pipeline().addLast(new LoginAuthRespHandler());
//                            ch.pipeline().addLast("HeartBeatHandler", new HeartBeatRespHandler());
                        }
                    });

            // 绑定端口，并同步等待绑定操作完成
            ChannelFuture f = b.bind("127.0.0.1", 8888).sync();
            System.out.println("Netty server start ok : " + "127.0.0.1" + ":" + 8888);
            // 等待服务端链路关闭之后，main方法才退出
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        new NettyServer().bind();
    }

}
