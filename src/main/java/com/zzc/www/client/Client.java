package com.zzc.www.client;

import com.zzc.www.server.NettyServer;

/**
 * Created by pc on 2017/5/10.
 */
public class Client {

    public static void main(String[] args){
        NettyClient nettyClient = new NettyClient(9999,"localhost");

    }
}
