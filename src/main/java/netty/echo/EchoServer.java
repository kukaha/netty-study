package netty.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new EchoServer(8888).run();
    }

    public void run() throws Exception {
        // NioEventLoopGroup线程组，用于接收客户端的连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 用于进行SocketChanel的网络读写
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();// Server端的NIO的辅助启动类
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// 对应JDK
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    // NIO类库中的ServerSocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            // FixedLengthFrameDecoder+StringDecoder来解决粘包问题
                            ch.pipeline().addLast(new FixedLengthFrameDecoder(10));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new EchoServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置TCP一些参数
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定端口，并同步等待绑定操作完成
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端链路关闭之后，main方法才退出
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
