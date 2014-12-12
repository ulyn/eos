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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RemoteHelper;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import com.sunsharing.eos.manager.zookeeper.ServiceCache;
import org.apache.log4j.Logger;

import java.util.Random;

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
            //取得服务的ip和port
            JSONArray jsonArray = ServiceCache.getInstance().getServiceData(appId, serviceId, serviceVersion);
            if (jsonArray == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION,
                        "eos没有取到在线的服务端！appId=" + appId + ",serviceId=" + serviceId + ",version=" + serviceVersion);
            }
            JSONObject config = null;
            if (!StringUtils.isBlank(req.getDebugServerIp()) && !Constants.EOS_MODE_PRO.equalsIgnoreCase(SysProp.eosMode)) {
                //走联调模式
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jo = jsonArray.getJSONObject(i);
                    if (jo.getString("ip").equals(req.getDebugServerIp())) {
                        config = jo;
                        break;
                    }
                }
                if (config == null) {
                    throw new RpcException(RpcException.DEBUG_SERVER_OUTLINE_EXCEPTION, "没有找到联调服务器（" + req.getDebugServerIp() + "）在线");
                }
            } else {
                Random r = new Random();
                int size = jsonArray.size();
                int index = r.nextInt(size);
                if (index >= size) {
                    index = 0;
                }
                config = jsonArray.getJSONObject(index);
            }
            String ip = config.getString("ip");
            int port = config.getIntValue("port");
            int timeout = config.getIntValue("timeout");
            String transporter = config.getString("transporter");

            RemoteHelper remoteHelper = new RemoteHelper();
            ServiceRequest serviceRequest = new ServiceRequest(req, transporter, timeout);
            ResponsePro responsePro = remoteHelper.call(serviceRequest, ip, port).getResponsePro();

            res.setStatus(responsePro.getStatus());
            res.setResultBytes(responsePro.getResultBytes());
            processChain.doProcess(req, res, processChain);
        } catch (Throwable e) {
            logger.error("", e);
            res.setExceptionResult(e);
        }
    }
}

