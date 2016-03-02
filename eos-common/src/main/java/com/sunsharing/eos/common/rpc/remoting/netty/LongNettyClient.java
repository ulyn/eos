package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
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
        LongChannel longChannel = ClientCache.getChannel(this, ip, port + "");
        ResponsePro responsePro = getResult(request.toRequestPro(), longChannel, request.getTimeout());
        return ServiceResponse.createServiceResponse(responsePro);
    }
}
