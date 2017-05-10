package com.zzc.www.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by pc on 2017/5/10.
 */
public class NettyServer implements Runnable{

    Logger logger = Logger.getLogger(NettyServer.class);

    NettyServerHandler nettyServerHandler = new NettyServerHandler();

    private int port;

    public NettyServer(int port) {
        this.port = port;
        new Thread(this).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runServerCMD();
    }

    private void runServerCMD(){
        String message;
        Scanner scanner = new Scanner(System.in);
        System.out.println("聊天已建立,开始聊天...");
        try {
            do{
                message=scanner.nextLine();
            }
            while (nettyServerHandler.sendMsg(message));
            System.out.println("聊天已断开...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        bind();
    }

    private void bind(){

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        logger.info("-------------start port---------------");

        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,worker);
            serverBootstrap.channel(NioServerSocketChannel.class);
            serverBootstrap.option(ChannelOption.SO_BACKLOG,1024);
            serverBootstrap.option(ChannelOption.TCP_NODELAY,true);
            serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline p = socketChannel.pipeline();
                    p.addLast(nettyServerHandler);
                }
            });

            ChannelFuture future = serverBootstrap.bind(port).sync();
            if(future.isSuccess()){
                logger.debug("start success in port : "+port);
            }
            future.channel().closeFuture().sync();
        }catch (Exception e){
                logger.error("error : "+e.getMessage());
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }
}
