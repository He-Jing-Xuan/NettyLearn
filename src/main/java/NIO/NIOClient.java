package NIO;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOClient {
    public static void main(String[] args) throws Exception{
        //得到一个服务器通道
        SocketChannel socketChannel=SocketChannel.open();
        //非阻塞通道
        socketChannel.configureBlocking(false);

        //提供服务器端的ip和端口
        InetSocketAddress inetSocketAddress=new InetSocketAddress("127.0.0.1",6666);
        //连接服务器
        if(!socketChannel.connect(inetSocketAddress)){
            while (socketChannel.finishConnect()){
                System.out.println("因为连接需要时间，客户端不会阻塞");
            }
        }else {
            //连接成功 发送数据
            String h="hhhh";
            ByteBuffer buffer=ByteBuffer.wrap(h.getBytes());
            // 将buffer数据 写入到channel
            socketChannel.write(buffer);
            System.in.read();
        }
    }
}
