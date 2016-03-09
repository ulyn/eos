package com.sunsharing.eos.common.rpc.remoting.netty.channel;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by criss on 14-2-20.
 */
public class ServerCache {

    static Logger logger = Logger.getLogger(ServerCache.class);

    static List<ServerChannel> serverChannels = new ArrayList<ServerChannel>();
    static boolean start = false;
    public static void addChannel(Channel ch)
    {
        synchronized (serverChannels)
        {
            ServerChannel sc = new ServerChannel();
            sc.channel = ch;
            boolean contains = false;
            for(ServerChannel ss:serverChannels)
            {
                if(ss.channel==ch)
                {
                    contains = true;
                    ss.refreshHeartBeat();
                    break;
                }
            }
            if(!contains)
            {
                serverChannels.add(sc);
            }
            if(!start)
            {
                start();
            }
        }
        logger.info("添加后通道数:"+serverChannels.size());
    }
    public static ServerChannel getChannel(Channel serverChannel)
    {
        synchronized (serverChannels)
        {
            for(ServerChannel sc:serverChannels)
            {
                if(sc.channel == serverChannel)
                {
                    return sc;
                }
            }
        }
        return null;
    }

    public static void removeChannel(Channel serverChannel)
    {
        synchronized (serverChannels)
        {
            for(ServerChannel sc:serverChannels)
            {
                if(sc.channel == serverChannel)
                {
                    try
                    {
                        sc.channel.close();
                    }catch (Exception e)
                    {

                    }
                    serverChannels.remove(sc);
                    break;
                }
            }
        }
        logger.info("删除后通道数:"+serverChannels.size());
    }

    private static ScheduledExecutorService heartscheduler = Executors.newScheduledThreadPool(1);
    private static ScheduledFuture beeperHandle = null;
    public static void start()
    {
        start  = true;
        beeperHandle = heartscheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                List<ServerChannel> deleteCache = new ArrayList<ServerChannel>();
                synchronized (serverChannels)
                {

                    for(ServerChannel sc:serverChannels)
                    {
                        if((new Date().getTime()-sc.heartBeat)>30000)
                        {
                            logger.error(sc.channel.getRemoteAddress()+":超过三十秒没有响应了");
                            deleteCache.add(sc);
                        }
                    }
                }
                for(ServerChannel sc:deleteCache)
                {
                    try
                    {
                        sc.channel.close();
                    }catch (Exception e)
                    {

                    }
                    logger.error(sc.channel.getRemoteAddress()+":要删除了");
                }
            }
        },10,10, TimeUnit.SECONDS);
    }

}
