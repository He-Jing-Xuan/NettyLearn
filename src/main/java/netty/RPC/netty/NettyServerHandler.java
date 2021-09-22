package netty.RPC.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.RPC.Provider.HelloServiceImpl;

public class NettyServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       // 获取到客户端的消息 并调用服务
        System.out.println("msg="+msg);
        //客户端在调用服务器的api 时，我们需要定义一个协议
        //比如要求，每次发消息时，都必须以某个字符串开头 "HelloService#hello#你好"
        if (msg.toString().startsWith("HelloService#hello#")) {
            String result = new HelloServiceImpl().hello(msg.toString().substring(msg.toString().lastIndexOf("#") + 1));
            //调用的结果返回给客户端
            ctx.writeAndFlush(result);
        }


    }
}
