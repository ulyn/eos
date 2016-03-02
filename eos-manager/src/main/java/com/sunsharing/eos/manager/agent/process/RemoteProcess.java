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
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RpcClientFactory;
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
    public void doProcess(RequestPro req, ResponsePro res, ProcessChain processChain) throws RpcException{
        try {
            String appId = req.getAppId();
            String serviceId = req.getServiceId();
            String methodVersion = req.getMethodVersion();
            String method = req.getMethod();
            //取得服务的ip和port
            JSONArray jsonArray = ServiceCache.getInstance().getServiceData(appId, serviceId,method, methodVersion);
            if (jsonArray == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION,
                        String.format("eos没有取到在线的服务端！appId=%s,serviceId=%s,method=%s,version=%s",
                                appId,serviceId,method,methodVersion));
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
            String transporter = req.getTransporter();

            logger.info(String.format("request target %s:%s:%s-%s-%s-%s", ip, port,
                    req.getAppId(), req.getServiceId(),req.getMethod(), req.getMethodVersion()));

            ResponsePro responsePro = RpcClientFactory.create(transporter).doRpc(req, ip, port);
            res.setEosVersion(responsePro.getEosVersion());
            res.setMsgId(responsePro.getMsgId());
            res.setSerialization(responsePro.getSerialization());
            res.setAction(responsePro.getAction());
            res.setStatus(responsePro.getStatus());
            res.setResultBytes(responsePro.getResultBytes());
            processChain.doProcess(req, res, processChain);
        } catch (Throwable e) {
            String str = String.format("Manager调用Server异常，%s:%s:%s:%s",
                    req.getAppId(), req.getServiceId(), req.getMethod(), req.getMethodVersion());
            logger.error(str, e);
            throw new RpcException(RpcException.REFLECT_INVOKE_EXCEPTION,str,e);
        }
    }
}

