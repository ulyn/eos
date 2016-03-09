package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.ClientCache;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.LongChannel;

/**
 * Created by criss on 14-2-19.
 */
public class LongNettyClient extends NettyClient {

    @Override
    public ServiceResponse doRpc(ServiceRequest request, String ip, int port) throws Throwable {
        ResponsePro responsePro = doRpc(request.toRequestPro(),ip,port);
        return ServiceResponse.createServiceResponse(responsePro);
    }

    @Override
    public ResponsePro doRpc(RequestPro request, String ip, int port) throws Throwable {
        LongChannel longChannel = ClientCache.getChannel(this, ip, port + "");
        if(!longChannel.channelWriteAble())
        {
            throw new RuntimeException("连接服务方：IP:"+ip+",port:"+port+",出现异常，无法写，请检查Server是否启动，" +
                    "或者localIP配错");
        }
        ResponsePro responsePro = getResult(request, longChannel, request.getTimeout());
        return responsePro;
    }
}
