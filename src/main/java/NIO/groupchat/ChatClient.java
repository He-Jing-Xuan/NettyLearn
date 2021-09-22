package NIO.groupchat;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class ChatClient {
    private final String HOST="127.0.0.1";
    private final int PORT=6667;
    private Selector selector;
    private SocketChannel socketChannel;
    private String username;
    public ChatClient() throws Exception{
        selector=Selector.open();
        //连接服务器
        socketChannel=SocketChannel.open(new InetSocketAddress(HOST,PORT));
        //设置肥猪色
        socketChannel.configureBlocking(false);
        //通道注册到selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到username
        username=socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(username+" is OK!");
    }
    //向服务器发送消息
    public void sendInfo(String info){
        info=username+" 说： "+info;
        try {
            //向通道发送消息
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void  readInfo(){
        try {
            int readChannels=selector.select();
            if(readChannels>0){
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator=selectionKeys.iterator();
                while (iterator.hasNext()){
                    SelectionKey key=iterator.next();
                    if(key.isReadable()){
                        //得到相关通道
                        SocketChannel sc=(SocketChannel) key.channel();
                        //得到一个Buffer
                        ByteBuffer buffer=ByteBuffer.allocate(1024);
                        //将通道的数据 写入缓冲区中
                        sc.read(buffer);
                        String msg=new String(buffer.array());
                        System.out.println(msg.trim());

                    }

                }
                iterator.remove();

            }

        }catch (Exception e){
//            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception{
        final ChatClient chatClient=new ChatClient();
        //启动线程
        new Thread(){
            public void run(){
                while (true){
                    chatClient.readInfo();
                    try {
                        Thread.currentThread().sleep(3000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        // 接收数据
        Scanner sc=new Scanner(System.in);
        while (sc.hasNextLine()){
            String s=sc.nextLine();
            chatClient.sendInfo(s);
        }
    }
}
