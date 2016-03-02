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

    @Override
    public ServiceResponse doRpc(ServiceRequest request, String ip, int port) throws Throwable {
        ResponsePro responsePro = doRpc(request.toRequestPro(), ip, port);
        return ServiceResponse.createServiceResponse(responsePro);
    }

    @Override
    public ResponsePro doRpc(RequestPro request, String ip, int port) throws Throwable {
        Channel channel = connect(ip, port);
        logger.debug("client is connected to netty server " + ip + ":" + port);
        ShortChannel shortChannel = new ShortChannel();
        shortChannel.setChannel(channel);
        try {
            ResponsePro responsePro = getResult(request, shortChannel, request.getTimeout());
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
