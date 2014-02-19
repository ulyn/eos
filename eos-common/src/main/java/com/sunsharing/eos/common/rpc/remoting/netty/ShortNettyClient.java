package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import org.jboss.netty.channel.Channel;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.*;

/**
 * Created by criss on 14-2-19.
 */
public class ShortNettyClient extends NettyClient {

    @Override
    public ResponsePro doRpc(RequestPro pro, String ip, int port, int timeout) throws Throwable {

        Channel channel = connect(ip,port);
        logger.debug("client is connected to netty server " + ip + ":" + port);
        ShortChannel shortChannel = new ShortChannel();
        shortChannel.setChannel(channel);
        try
        {
            return getResult(pro,shortChannel,timeout);
        }catch (Exception e)
        {
            logger.error("请求出错！", e);
            throw e;
        }finally {
            if (channel != null) {
                channel.close();
            }
        }
    }

}
