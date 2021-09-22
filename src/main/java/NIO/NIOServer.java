package NIO;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NIOServer {
    public static void main(String[] args) throws Exception{
        // 创建一个网络服务通道
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        //得到selector对象
        Selector selector=Selector.open();
        //绑定端口，进行监听
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));
        //设置为肥猪色
        serverSocketChannel.configureBlocking(false);
        //将通道注册到selector
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //循环等待客户端连接
        while(true){
            //等待一秒，若没有事件发生 就返回
            if(selector.select(1000)==0){
                System.out.println("服务器等待1秒，无连接");
                continue;
            }
            //如果返回的大于0  表示 已经获取到关注的事件
            //得到 关注的事件的集合
            //通过key 反向获取到 通道
            Set<SelectionKey> selectionKeys=selector.selectedKeys();
            //遍历key
            Iterator<SelectionKey> iterator=selectionKeys.iterator();
            while ((iterator.hasNext())){
                //获取到key
                SelectionKey key=iterator.next();
                //根据key 对应的通道 发生的事件左对应的处理
                //  如果是 OP_ACCEPT有新的客户端连接
                if(key.isAcceptable()){
                    //有客户连接 生成连接的通过
                    SocketChannel socketChannel=serverSocketChannel.accept();
                    System.out.println("客户端连接成功，生成了一个连接"+socketChannel.hashCode());
                    //将通道设置为非阻塞
                    socketChannel.configureBlocking(false);
                    //将通道 注册到selector,  关注事件为 OP_READ，
                    //同时关联一个buffer
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));

                }
                //可读事件
                if(key.isReadable()){
                    //通过key反向获取到对应的通道
                  SocketChannel channel =(SocketChannel) key.channel();
                  //获取到与通过关联的buffer
                    ByteBuffer buffer=(ByteBuffer) key.attachment();
                    channel.read(buffer);
                    System.out.println(" from 客户端"+ new String(buffer.array()));
                }
                //手动移除 selectionKey 防止重复操作
                iterator.remove();
            }
        }
    }
}
