package com.sunsharing.eos.common.rpc.remoting.netty.channel;


import org.jboss.netty.channel.Channel;

/**
 * Created by criss on 14-2-19.
 */
public class ShortChannel implements MyChannel {

    Channel channel;

    public void write(Object obj)
    {
        channel.write(obj);
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}
