package netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * 自定义handler
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {
    /**
     *读取客户端发送过来的消息
     * @param ctx 上下文对象，含有 管道pipeline，通道channel，地址
     * @param msg 就是客户端发送的数据，默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        //任务队列
        // 执行某任务耗时长时，就使用到任务队列中
        //解决方案1 用户自定义普通任务
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                //执行耗时长的任务
                // 逻辑t
                try {
                    Thread.sleep(1000*10);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端 我是异步任务1",CharsetUtil.UTF_8));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
        //用户自定义定时任务  将任务 提交到scheduleTaskQueue
        /**
         * 该方式会将任务提交到scheduleTaskQueue定时任务队列中。
         * 该队列是底层是优先队列PriorityQueue实现的，固该队列中的任务会按照时间的先后顺序定时执行。
         */
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
                //执行耗时长的任务
                // 逻辑t
                try {
                    Thread.sleep(1000*10);
                    ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端 我是异步任务1",CharsetUtil.UTF_8));
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        },5, TimeUnit.SECONDS);

//        System.out.println("服务器读取线程：" + Thread.currentThread().getName());
//        System.out.println("server ctx = " + ctx);
//        //看看Channel和Pipeline的关系
//        Channel channel = ctx.channel();
//        ChannelPipeline pipeline = ctx.pipeline(); //本质是个双向链表，出栈入栈
//
//        //将msg转成一个ByteBuf，比NIO的ByteBuffer性能更高
        ByteBuf buf = (ByteBuf)msg;
        System.out.println("客户端发送的消息是：" + buf.toString(CharsetUtil.UTF_8));
//        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    //数据读取完毕
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //它是 write + flush，将数据写入到缓存buffer，并将buffer中的数据flush进通道
        //一般讲，我们对这个发送的数据进行编码
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello, 客户端~，我已经读取到你的数据了", CharsetUtil.UTF_8));
    }

    //处理异常，一般是关闭通道
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
