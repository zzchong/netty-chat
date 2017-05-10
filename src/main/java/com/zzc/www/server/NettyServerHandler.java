package com.zzc.www.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by pc on 2017/5/10.
 */
public class NettyServerHandler extends ChannelHandlerAdapter {

    Logger logger = Logger.getLogger(NettyServerHandler.class);

    private ChannelHandlerContext ctx;

    /**
     * 发送消息
     */
    public boolean sendMsg(String msg) throws IOException {
//        logger.info("server : "+msg);
//        ctx.writeAndFlush(msg);
        ctx.writeAndFlush(getSendByteBuf(msg));
        return !msg.equals("!q");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        logger.info("有客户端连接："+ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;

        String recieved = getMessage(buf);
        System.out.println("client : "+recieved);

//        try {
//            ctx.writeAndFlush(getSendByteBuf("APPLE"));
//        }catch (UnsupportedEncodingException e){
//            logger.error("error : "+e.getMessage());
//            e.printStackTrace();
//        }
    }


    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("与客户端断开连接...");
//        cause.printStackTrace();
        ctx.close();
    }

    /*
	 * 从ByteBuf中获取信息 使用UTF-8编码返回
	 */
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

    private ByteBuf getSendByteBuf(String message)
            throws UnsupportedEncodingException {

        byte[] req = message.getBytes("UTF-8");
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }
}

