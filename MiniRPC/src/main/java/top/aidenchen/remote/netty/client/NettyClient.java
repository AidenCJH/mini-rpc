package top.aidenchen.remote.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.util.CharsetUtil;
import top.aidenchen.common.Invocation;

import java.net.InetSocketAddress;

public class NettyClient {

    public static void run(String host, int port, Invocation invocation) {
        /**
         * @Description 配置相应的参数，提供连接到远端的方法
         **/
        EventLoopGroup group = new NioEventLoopGroup();//I/O线程池
        try {
            Bootstrap bs = new Bootstrap();//客户端辅助启动类
            bs.group(group)
                    .channel(NioSocketChannel.class)//实例化一个Channel
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 初始化通道
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ObjectEncoder());
                            pipeline.addLast(new ObjectDecoder(10240, ClassResolvers.cacheDisabled(this.getClass().getClassLoader())));
                            pipeline.addLast(new NettyClientHandle());

                        }
                    });

            //连接到远程节点；等待连接完成
            ChannelFuture future = bs.connect().sync();
            //发送消息到服务器端
            future.channel().writeAndFlush(invocation);
            //阻塞操作，closeFuture()开启了一个channel的监听器（这期间channel在进行各项工作），直到链路断开
            future.channel().closeFuture().sync();
            group.shutdownGracefully().sync();
        } catch (Exception e) {
            throw new RuntimeException("发送失败");
        }
    }
}
