package com.zzc.www.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by pc on 2017/5/10.
 */
public class NettyClientHandler extends ChannelHandlerAdapter {

    Logger logger = Logger.getLogger(NettyClientHandler.class);
    private ChannelHandlerContext ctx;
    private  ByteBuf firstMessage;


    /**
     * 发送消息

     */
    public boolean sendMsg(String msg) throws IOException {
        byte[] data = msg.getBytes();
        firstMessage=Unpooled.buffer();
        firstMessage.writeBytes(data);
//        ctx.writeAndFlush(firstMessage);
        ctx.channel().writeAndFlush(firstMessage);
        return !msg.equals("!q");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
//        byte[] data = "服务器，给我一个APPLE".getBytes();
//        firstMessage=Unpooled.buffer();
//        firstMessage.writeBytes(data);
//        ctx.writeAndFlush(firstMessage);
        sendMsg("connect success...");
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String rev = getMessage(buf);
        System.out.println("server : " + rev);
    }

    /**
     * 发生异常时调用
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("与服务器断开连接...");
        ctx.close();
    }

    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        try {
            return new String(con, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

