package NIO.groupchat;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class ChatServer {
    private Selector selector;
    private ServerSocketChannel listenchannel;
    private static final int PORT=6667;
    //初始化
    public  ChatServer() {
        try {
            //得到选择器
            selector = Selector.open();
            listenchannel = ServerSocketChannel.open();
            //绑定端口
            listenchannel.socket().bind(new InetSocketAddress(PORT));
            //设置非阻塞模式
            listenchannel.configureBlocking(false);
            //将 通道注册进 选择器
            listenchannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //监听
    public void listen(){
        try {
            while (true) {
                int count=selector.select();
            if(count>0){
                //有事件需要处理
                Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey selectionKey=iterator.next();
                    //根据事件类型 执行响应的处理逻辑 有事件请求到达
                    if(selectionKey.isAcceptable()){
                        //
                        SocketChannel channel=listenchannel.accept();
                        //设置非阻塞
                        channel.configureBlocking(false);
                        //将通道注册到选择器
                        channel.register(selector,SelectionKey.OP_READ);

                        System.out.println(channel.getRemoteAddress()+"上线");
                    }
                    //通道可以读取
                    if(selectionKey.isReadable()){
                        //处理通道的读取
                        readData(selectionKey);
                    }

                }
                //当前key删除，防止重复处理
                iterator.remove();
            }else {
                System.out.println("等待。。。。。。。");
            }
            }
        }catch (Exception e){
             e.printStackTrace();
            }
    }
    //读取 客户端消息
    private void  readData(SelectionKey key){
        SocketChannel channel=null;
        try {
            //得到channel
            channel=(SocketChannel) key.channel();
            //创建缓冲
            ByteBuffer buffer=ByteBuffer.allocate(1024);

            int count=channel.read(buffer);
            if(count>0){
                String msg=new String(buffer.array());
                System.out.println("from 客户端"+msg);
                //向其他客户端转发消息
               sendInfoToOtherClients(msg,channel);
            }
        }catch (Exception e){
            try {
                System.out.println(channel.getRemoteAddress()+"离线了");
                //取消注册
                key.cancel();
                //关闭通道
                channel.close();
            }catch (IOException ee){
                ee.printStackTrace();
            }

            e.printStackTrace();
        }
    }
    //转发消息
    private  void sendInfoToOtherClients(String msg,SocketChannel self) throws  Exception{
        System.out.println("服务器转发消息中。。。");
        // 遍历所有注册到selector上的通道，并排除自己
        for(SelectionKey key:selector.keys()){
            //通过key得到通道
            Channel targetChannel=key.channel();
            if(targetChannel instanceof SocketChannel &&targetChannel!=self){
                SocketChannel dest=(SocketChannel) targetChannel;
                //将消息存储到buffer中
                ByteBuffer buffer=ByteBuffer.wrap(msg.getBytes());
                //将buffer数据写入到通道
                dest.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        ChatServer chatServer=new ChatServer();
        //监听
        chatServer.listen();
    }
}
