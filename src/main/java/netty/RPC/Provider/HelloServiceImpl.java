package netty.RPC.Provider;

import netty.RPC.commonInterface.HelloService;

public class HelloServiceImpl implements HelloService {
    //消费方调用该方法时，就返回一个结果
    @Override
    public String hello(String msg) {
        System.out.println("收到客户端消息：["+msg+"]");
        if(msg!=null){
            return  "已收到消息"+msg;
        }
        else
         return "你的消息为空";
    }
}
