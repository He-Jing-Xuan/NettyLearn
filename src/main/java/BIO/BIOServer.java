package BIO;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIOServer  {
    public static void main(String[] args) throws Exception{
        //线程池机制
        //思路
        /**
         * 创建一个线程池
         * 如果有客户端连接，就创建一个线程
         *
         */
        ExecutorService executorService= Executors.newCachedThreadPool();
        //创建ServerSocket
        ServerSocket serverSocket=new ServerSocket(6666);
        System.out.println("服务器启动");

        while(true){
            //监听服务器端口
            final Socket socket=serverSocket.accept();
            System.out.println("连接到客户端");
            //创建一个线程 与之单独通信
            executorService.execute(new Runnable() {
                public void run() {
                    //进行通信
                    handler(socket);

                }
            });
        }
    }
    public  static  void handler(Socket socket) {
        byte [] bytes=new byte[1024];

        try {
            //通过socket获取输入流
            InputStream inputStream=socket.getInputStream();
            //循环读取客户端输入的数据
            while(true){
                System.out.println("线程信息ID :"+Thread.currentThread().getId());
                int read=inputStream.read(bytes);
                if(read!=-1){
                    System.out.println(new String(bytes,0,read));
                }else {
                    break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            //关闭连接
            try {
                socket.close();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

}
