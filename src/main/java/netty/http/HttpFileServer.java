package netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

public class HttpFileServer {

    private static final String DEFAULT_URL = "/src";

    private final int port;

    public HttpFileServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        new HttpFileServer(8888).run();
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
                    // NIO类库中的ServerSocketChannel
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                            ch.pipeline().addLast("fileServerHandler", new HttpFileServerHandler(DEFAULT_URL));
                        }
                    });

            // 绑定端口，并同步等待绑定操作完成
            ChannelFuture f = b.bind("192.168.1.40", port).sync();
            System.out.println("Http 文件目录服务器启动，网址是 http://192.168.1.40:" + port + DEFAULT_URL);

            // 等待服务端链路关闭之后，main方法才退出
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
