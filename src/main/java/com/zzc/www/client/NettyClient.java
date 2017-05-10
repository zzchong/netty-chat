package com.zzc.www.client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created by pc on 2017/5/10.
 */
public class NettyClient implements Runnable{

    Logger logger = Logger.getLogger(NettyClient.class);

    private NettyClientHandler clientHandler = new NettyClientHandler();

    private int port;

    private String host;

    public NettyClient(int port, String host) {
        this.port = port;
        this.host = host;
        new Thread(this).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runClientCMD();
    }

    private void runClientCMD(){
        //服务端主动推送消息
        String message;
        Scanner scanner = new Scanner(System.in);
        System.out.println("聊天已连接....");
        try {
            do{
                message=scanner.nextLine();
            } while (clientHandler.sendMsg(message));
            System.out.println("聊天断开...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        start();
    }

    private void start() {

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.group(eventLoopGroup);
            bootstrap.remoteAddress(host, port);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(clientHandler);
                }
            });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            if (future.isSuccess()) {
                logger.debug("----------------connect server success----------------");
            }
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("error : "+e.getMessage());
            e.printStackTrace();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
