package netty.buf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class NettyByteBuf {
    public static void main(String[] args) {
        //创建一个ByteBuf
//1、创建对象，该对象包含一个数组，是一个byte[10]
//2、在netty的buffer中，写入数据后再读取数据不需要使用 flip 进行反转
// 底层维护了 readerIndex 和 writeIndex
//往buffer中写的范围为 [writeIndex, capacity)
//往buffer中可读的范围为 [readerIndex, writeIndex)。使用 buf.readByte() 会往后移动 readerIndex 指针，使用 buf.getByte(i) 通过索引获取就不会移动该指针
        ByteBuf byteBuf = Unpooled.buffer(10);
        for (int i = 0; i < 10; i++) {
            byteBuf.writeByte(i);
        }
//获取该buf的大小
        int capacity = byteBuf.capacity();
//输出
        for (int i = 0; i < byteBuf.capacity(); i++) {
            System.out.println(byteBuf.getByte(i));
            System.out.println(byteBuf.readByte());
        }
        byte[] content = byteBuf.array();
//将content转成字符串
        String c = new String(content, StandardCharsets.UTF_8);
//数组偏移量
        int offset = byteBuf.arrayOffset();
//获取读取偏移量
        int readerIndex = byteBuf.readerIndex();
//获取写偏移量
        int writerIndex = byteBuf.writerIndex();
//获取容量
       // int capacity = byteBuf.capacity();
//获取可读取的字节数
        int readableBytes = byteBuf.readableBytes();
//通过索引获取某个位置的字节
        byte aByte = byteBuf.getByte(0);
//获取Buf中某个范围的字符序列
        CharSequence charSequence = byteBuf.getCharSequence(0, 4, StandardCharsets.UTF_8);


    }
}
