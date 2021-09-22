package netty.RPC.Provider;

import netty.RPC.netty.NettyServer;

//启动服务提供者
public class ServerBootStrap {
    public static void main(String[] args) {
        NettyServer.startServer("127.0.0.1",8888);
    }
}
