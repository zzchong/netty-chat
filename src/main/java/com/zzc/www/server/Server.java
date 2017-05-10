package com.zzc.www.server;

import com.zzc.www.client.NettyClient;

/**
 * Created by pc on 2017/5/10.
 */
public class Server {

    public static void main(String[] args){
        NettyServer nettyServer = new NettyServer(9999);
    }
}
