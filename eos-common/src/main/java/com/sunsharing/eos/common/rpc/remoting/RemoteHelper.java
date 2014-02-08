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
import com.sunsharing.eos.common.rpc.RpcClient;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.netty.NettyClient;
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

    public ResponsePro call(RequestPro requestPro, String ip, int port, String transporter, int timeout) throws Throwable {
        RpcClient client = null;
        if ("netty".equalsIgnoreCase(transporter)) {
            client = new NettyClient();
        }
        return client.doRpc(requestPro, ip, port, timeout);
    }

}

