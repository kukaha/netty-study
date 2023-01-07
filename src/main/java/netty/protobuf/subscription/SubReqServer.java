package netty.protobuf.subscription;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import netty.protobuf.SubscribeReqProto;

public class SubReqServer {

    private final int port;

    public SubReqServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new SubReqServer(8888).run();
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
                            ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                            ch.pipeline().addLast(
                                    new ProtobufDecoder(
                                            SubscribeReqProto
                                                    .SubscribeReq
                                                    .getDefaultInstance()));
                            ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                            ch.pipeline().addLast(new ProtobufEncoder());
                            ch.pipeline().addLast(new SubReqServerHandler());
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
