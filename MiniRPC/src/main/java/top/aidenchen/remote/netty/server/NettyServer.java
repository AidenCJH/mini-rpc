package top.aidenchen.remote.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

public class NettyServer {
    private int port;

    public NettyServer(int port) {
        this.port = port;
    }

    public void run() {
//        Netty的Reactor线程池，初始化了一个NioEventLoop数组，用来处理I/O操作,如接受新的连接和读/写数据
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new ObjectDecoder(10240, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            socketChannel.pipeline().addLast(new NettyServerHandle());
                            socketChannel.pipeline().addLast(new ObjectEncoder());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            channelFuture.channel().closeFuture().sync();
            group.shutdownGracefully().sync();
        } catch (Exception e) {
            throw new RuntimeException("启动服务端失败");
        }
    }
}
