/**
 * @(#)AbstractProxy
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午11:35
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.proxy;

import com.sunsharing.eos.client.rpc.ClientFactory;
import com.sunsharing.eos.client.rpc.SocketClient;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.rpc.Client;
import com.sunsharing.eos.common.rpc.ClientProxy;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.impl.RpcResult;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public abstract class AbstractProxy implements ClientProxy {

    protected Object getRpcResult(RpcInvocation invocation, ServiceConfig config) throws Throwable {
        //zookeeper取得服务的url
        String url = "localhost";
        int port = 20383;

        Client client = ClientFactory.getClient(config.getTransporter());
        RpcResult rpcResult = client.doRpc(invocation, config.getSerialization(), url, port);
        if (rpcResult.hasException()) {
            throw rpcResult.getException();
        } else return rpcResult.getValue();
    }
}

