/**
 * @(#)RemoteProcess
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午3:19
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.process;

import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RemoteHelper;
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
public class RemoteProcess implements Process {
    Logger logger = Logger.getLogger(RemoteProcess.class);

    @Override
    public void doProcess(RequestPro req, ResponsePro res, ProcessChain processChain) {
        try {
            res.setSerialization(res.getSerialization());
            String appId = req.getAppId();
            String serviceId = req.getServiceId();
            String serviceVersion = req.getServiceVersion();
            //todo 取得服务的ip和port
            String ip = "localhost";
            int port = 2000;
            int timeout = 10000;
            String transporter = "netty";

            RemoteHelper remoteHelper = new RemoteHelper();
            ResponsePro responsePro = remoteHelper.call(req, ip, port, transporter, timeout);
            res.setResultBytes(responsePro.getResultBytes());
        } catch (Throwable e) {
            try {
                res.setResult(new RpcResult(e));
            } catch (Exception e1) {
                logger.error("eos代理返回异常结果序列化出错！", e);
                res.setStatus((byte) 1);
            }
        }
    }
}

