/**
 * @(#)ACLProcess
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

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RemoteHelper;
import com.sunsharing.eos.manager.sys.SysProp;
import com.sunsharing.eos.manager.zookeeper.ServiceCache;
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
public class ACLProcess implements Process {
    Logger logger = Logger.getLogger(ACLProcess.class);

    @Override
    public void doProcess(RequestPro req, ResponsePro res, ProcessChain processChain) {
        if (SysProp.eosMode.equals(Constants.EOS_MODE_DEV)) {
            try {
                boolean acl = ServiceCache.getInstance().getACL(req.getAppId(), req.getServiceId(), req.getServiceVersion());
                if (acl) {
                    processChain.doProcess(req, res, processChain);
                } else {
                    String error = "服务调用失败，未通过审核的服务！"
                            + req.getAppId() + "-"
                            + req.getServiceId() + "-"
                            + req.getServiceVersion();
                    logger.info(error);
                    res.setExceptionResult(new RpcException(RpcException.FORBIDDEN_EXCEPTION, error));
                }
            } catch (Exception e) {
                String error = "权限验证异常!";
                logger.error(error, e);
                if(e.getMessage().startsWith("服务方更新提醒"))
                {
                    error = e.getMessage();
                }
                res.setExceptionResult(new RpcException(RpcException.FORBIDDEN_EXCEPTION, error));
            }
        }
    }

}

