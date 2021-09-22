package NIO;


import java.nio.IntBuffer;

public class BasicBuffer {
    public static void main(String[] args) {
        //创建一个buffer 大小为5
        IntBuffer intBuffer=IntBuffer.allocate(5);
        //向buffer存放数据
        for(int i=0;i<intBuffer.capacity();i++){
            intBuffer.put(i*2);
        }
        //取数据
        //读写模式切换
        intBuffer.flip();
        while(intBuffer.hasRemaining()){
            System.out.println(intBuffer.get());
        }
    }
}
