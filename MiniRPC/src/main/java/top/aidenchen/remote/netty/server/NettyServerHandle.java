package top.aidenchen.remote.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import top.aidenchen.common.Invocation;
import top.aidenchen.properties.PropertiesReader;
import top.aidenchen.register.LocalRegister;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class NettyServerHandle extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (PropertiesReader.get("server_log_work").equals("true")) {
            InetSocketAddress ipSocket = (InetSocketAddress)ctx.channel().remoteAddress();
            System.out.println("收到来自" + ipSocket.getAddress() + ":" + ipSocket.getPort() + "的调用");
        }
        //处理收到的数据
        Invocation invocation = (Invocation) msg;
        String interfaceName = invocation.getInterfaceName();
        Class implClass = LocalRegister.get(interfaceName);
        Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
        Object result = method.invoke(implClass.newInstance(), invocation.getParameters());
        //响应请求
        ctx.channel().writeAndFlush(method.getReturnType().cast(result));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //出现异常的时候执行的动作（打印并关闭通道）
        cause.printStackTrace();
        ctx.close();
    }
}
