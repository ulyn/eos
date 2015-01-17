/**
 * @(#)RpcClientFactory
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2015
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 15-1-17 下午3:42
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
import com.sunsharing.eos.common.rpc.RpcClient;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.remoting.netty.LongNettyClient;
import com.sunsharing.eos.common.rpc.remoting.netty.ShortNettyClient;

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
public class RpcClientFactory {

//    private static Logger logger = Logger.getLogger(RpcClientFactory.class);

    public static RpcClient create(String transporter) {
        RpcClient client = null;
        if (Constants.SHORT_NETTY.equalsIgnoreCase(transporter)) {
            client = new ShortNettyClient();
        } else if (Constants.LONG_NETTY.equalsIgnoreCase(transporter)) {
            client = new LongNettyClient();
        } else {
            throw new RpcException("没有指定的transporter实现：" + transporter);
        }
        return client;
    }

//    public ResponsePro call(ServiceRequest serviceRequest,
//                            String ip, int port) throws Throwable {
//
//        String transporter = serviceRequest.getTransporter();
//
//        RequestPro requestPro = serviceRequest.getRequestPro();
//        logger.info(String.format("request target %s:%s:%s-%s-%s", ip, port,
//                requestPro.getAppId(),
//                requestPro.getServiceId(),
//                requestPro.getServiceVersion()));
//        return client.doRpc(requestPro, ip, port,serviceRequest.getTimeout());
//    }
}

