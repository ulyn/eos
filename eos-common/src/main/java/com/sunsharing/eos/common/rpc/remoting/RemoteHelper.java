/**
 * @(#)RemoteHelper
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午3:37
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.remoting;

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
import com.sunsharing.eos.common.rpc.RpcClient;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.netty.LongNettyClient;
import com.sunsharing.eos.common.rpc.remoting.netty.NettyClient;
import com.sunsharing.eos.common.rpc.remoting.netty.ShortNettyClient;
import org.apache.log4j.Logger;

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
public class RemoteHelper {
    static Logger logger = Logger.getLogger(RemoteHelper.class);


    public ServiceResponse call(ServiceRequest serviceRequest,
                                String ip, int port) throws Throwable {
        RpcClient client = null;
        String transporter = serviceRequest.getTransporter();
        if (Constants.SHORT_NETTY.equalsIgnoreCase(transporter)) {
            client = new ShortNettyClient();
        } else if (Constants.LONG_NETTY.equalsIgnoreCase(transporter)) {
            client = new LongNettyClient();
        }
        RequestPro requestPro = serviceRequest.getRequestPro();
        logger.info(String.format("request target %s:%s:%s-%s-%s", ip, port,
                requestPro.getAppId(),
                requestPro.getServiceId(),
                requestPro.getServiceVersion()));
        return client.doRpc(serviceRequest, ip, port);
    }

}

