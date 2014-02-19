package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.ClientCache;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.LongChannel;

/**
 * Created by criss on 14-2-19.
 */
public class LongNettyClient extends NettyClient {
    @Override
    public ResponsePro doRpc(RequestPro pro, String ip, int port, int timeout) throws Throwable {
        LongChannel longChannel = ClientCache.getChannel(this,ip,port+"");
        return getResult(pro,longChannel,timeout);
    }
}
