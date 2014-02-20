package com.sunsharing.eos.common.rpc.remoting.netty.channel;

import org.jboss.netty.channel.Channel;

import java.util.Date;

/**
 * Created by criss on 14-2-20.
 */
public class ServerChannel {

    Channel channel;
    long heartBeat = new Date().getTime();

    public void refreshHeartBeat()
    {
        heartBeat = new Date().getTime();
    }
}
