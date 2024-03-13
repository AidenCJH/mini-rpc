package top.aidenchen.remote.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import top.aidenchen.common.Invocation;
import top.aidenchen.proxy.ProxyFactory;

public class NettyClientHandle extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * @Description 处理服务端响应
         **/
        ProxyFactory.result = msg;
        ctx.pipeline().close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        /**
         * @Description 处理I/O事件的异常
         **/
        cause.printStackTrace();
        ctx.close();
    }
}
