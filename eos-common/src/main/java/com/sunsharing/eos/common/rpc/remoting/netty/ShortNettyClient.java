package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import org.jboss.netty.channel.Channel;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.*;

/**
 * Created by criss on 14-2-19.
 */
public class ShortNettyClient extends NettyClient {

    /**
     * 执行远程调用的方法
     *
     * @param requestPro
     * @param ip
     * @param port
     * @param timeout
     * @return
     */
    @Override
    public ResponsePro doRpc(RequestPro requestPro, String ip, int port, int timeout) throws Throwable {
        Channel channel = connect(ip, port);
        logger.debug("client is connected to netty server " + ip + ":" + port);
        ShortChannel shortChannel = new ShortChannel();
        shortChannel.setChannel(channel);
        try {
            ResponsePro responsePro = getResult(requestPro, shortChannel, timeout);
            return responsePro;
        } catch (Exception e) {
            logger.error("请求出错！", e);
            throw e;
        } finally {
            if (channel != null) {
                channel.close();
            }
        }
    }
}
