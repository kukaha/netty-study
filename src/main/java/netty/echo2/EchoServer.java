package netty.echo2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * EchoServer 将原样返回 EchoClient 发来的任何消息
 */

public final class EchoServer {

    static final boolean SSL = System.getProperty("ssl") != null;

    static final int PORT = Integer.parseInt(
            System.getProperty("port", "8007")
    );


    public static void main(String[] args) throws Exception {
        // 通过 SslContext 构建安全套接字（Secure Socket），
        // 这是 Netty 提供的安全特性
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final EchoServerHandler serverHandler = new EchoServerHandler();

        try {
            ServerBootstrap b = new ServerBootstrap();
            // 设置线程组
            b.group(bossGroup, workerGroup)
                    // 说明服务器端通道的实现类（便于 Netty 做反射处理）
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    // 对服务端的 NioServerSocketChannel 添加 Handler
                    // LoggingHandler 是 netty 内置的一种 ChannelDuplexHandler，
                    // 既可以处理出站事件，又可以处理入站事件，即 LoggingHandler
                    // 既记录出站日志又记录入站日志。
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 对服务端接收到的、与客户端之间建立的 SocketChannel 添加 Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            if (sslCtx != null) {
                                // sslCtx.newHandler(ch.alloc())对传输的内容做安全加密处理
                                p.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            // 如果需要的话，可以用 LoggingHandler 记录与客户端之间的通信日志
                            // p.addLast(new LoggingHandler(LogLevel.INFO));
                            // serverHandler 用来实现 echo
                            p.addLast(serverHandler);
                        }
                    });

            // 启动服务器
            ChannelFuture f = b.bind(PORT).sync();

            // 等待服务端 NioServerSocketChannel 关闭
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}