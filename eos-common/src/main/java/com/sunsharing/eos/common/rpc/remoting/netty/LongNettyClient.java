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

    /**
     * 执行远程调用的方法
     *
     * @param requestPro
     * @param ip
     * @param port
     * @return
     */
    @Override
    public ResponsePro doRpc(RequestPro requestPro, String ip, int port, int timeout) throws Throwable {
        LongChannel longChannel = ClientCache.getChannel(this, ip, port + "");
        ResponsePro responsePro = getResult(requestPro, longChannel, timeout);
        return responsePro;
    }
}
