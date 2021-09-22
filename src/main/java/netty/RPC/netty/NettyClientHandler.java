package netty.RPC.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import jdk.nashorn.internal.codegen.CompilerConstants;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {
    private ChannelHandlerContext context; //上下文
    private String result; //返回的结果
    private String para; //客户端调用方法时，传入的参数
   //通过call 调用发送给服务端， call会先等待。 当服务端返回消息时， 返回到channelRead方法， 然后再唤醒

    /**
     * 连接建立之后 调用该方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context=ctx;
    }

    /**
     * 收到服务器的数据之后 调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       result=msg.toString();
       //唤醒等待的线程
       notify();
    }

    /**
     *   被代理对象的调用，真正发送数据给服务器，发送完后就阻塞，等待被唤醒（channelRead）
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        System.out.println("线程被调用-----");
        context.writeAndFlush(para);
        //进行wait
        wait(); //等待 channelRead 获取到服务器的结果后，进行唤醒。
        return result; //服务方返回的结果
    }
    public void setPara(String para){
        this.para = para;
    }
}
