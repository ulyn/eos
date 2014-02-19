package com.sunsharing.eos.common.rpc.remoting.netty.channel;

import com.sunsharing.eos.common.rpc.remoting.netty.NettyClient;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by criss on 14-2-19.
 */
public class ClientCache {

    static Logger logger = Logger.getLogger(ClientCache.class);

    public static List<LongChannel> channels = new ArrayList<LongChannel>();

    public static synchronized LongChannel getChannel(NettyClient nettyClient,String ip,String port)
    {
        logger.debug("当前连接数:"+channels.size());
        for(LongChannel ch:channels)
        {
            if(ch.getIp().equals(ip) && ch.getPort().equals(port))
            {
                return ch;
            }
        }
        logger.info("找不到Channel,重新连接");
        LongChannel channel = new LongChannel(nettyClient,ip,port);
        channel.connect();
        channel.start();
        channels.add(channel);
        logger.info("添加后连接数:"+channels.size());
        return channel;
    }

    public static LongChannel getChannel(Channel channel)
    {
        for(LongChannel ch:channels)
        {
            if(ch.getChannel()==channel)
            {
                return ch;
            }
        }
        return null;
    }

}
