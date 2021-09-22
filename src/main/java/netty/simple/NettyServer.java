package netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class NettyServer {
    public static void main(String[] args) throws Exception{
        //创建BossGroup和workerGroup
        //处理连接请求
        //子线程（EventLoop）的个数是CPU核数乘以2
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        // 真正的业务交给workerGroup
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            //创建服务器端的启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            //使用链式编程来进行设置，配置
            bootstrap.group(bossGroup, workerGroup) //设置两个线程组
                    .channel(NioServerSocketChannel.class) //使用 NioServerSocketChannel 作为服务器的通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) //设置线程队列得到连接个数
                    .childOption(ChannelOption.SO_KEEPALIVE, true) //设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() { //为accept channel的pipeline预添加的handler
                        //给 pipeline 添加处理器，每当有连接accept时，就会运行到此处。
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyServerHandler());
                        }
                    }); //给我们的 workerGroup 的 EventLoop 对应的管道设置处理器

            System.out.println("........服务器 is ready...");
            //绑定一个端口并且同步，生成了一个ChannelFuture 对象
            //启动服务器（并绑定端口）
            ChannelFuture future = bootstrap.bind(6668).sync();
            //给cf注册监听器，监听我们关心的事件
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isDone()){
                        System.out.println("监听端口成功");
                    }else {
                        System.out.println("监听失败");
                    }
                }
            });
            //对关闭通道进行监听
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
