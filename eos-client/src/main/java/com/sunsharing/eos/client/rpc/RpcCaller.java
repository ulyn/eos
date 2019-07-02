/**
 * @(#)ServiceInvoke
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-11 下午7:09
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */

package com.sunsharing.eos.client.rpc;

import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.client.sys.EosClientProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.filter.AbstractServiceFilter;
import com.sunsharing.eos.common.filter.FilterChain;
import com.sunsharing.eos.common.filter.ServiceFilterException;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RpcClientFactory;
import com.sunsharing.eos.common.utils.StringUtils;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>  执行调用的过滤器
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class RpcCaller extends AbstractServiceFilter {
    private Logger logger = Logger.getLogger(RpcCaller.class);

    public RpcCaller() {
    }


    /**
     * 执行过滤
     *
     * @param serviceRequest
     * @param serviceResponse
     * @param fc
     */
    @Override
    protected void doFilter(ServiceRequest serviceRequest,
                            ServiceResponse serviceResponse, FilterChain fc) throws ServiceFilterException, RpcException {
        try {
            ResponsePro responsePro = call(serviceRequest.toRequestPro());
            ServiceResponse response = ServiceResponse.createServiceResponse(responsePro);
            serviceResponse.copyForm(response);
        } catch (RpcException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RpcException(RpcException.NETWORK_EXCEPTION, throwable);
        }
        fc.doFilter(serviceRequest, serviceResponse);
    }

    public ResponsePro call(RequestPro requestPro) {
        //zookeeper取得服务的ip
        JSONObject jo = getEosLocation(requestPro.getAppId(), requestPro.getServiceId(),
            requestPro.getMethod(), requestPro.getMethodVersion());
        String ip = jo.getString("ip");
        int port = jo.getInteger("port");

        try {
            logger.debug(String.format("request %s-%s-%s-%s target eos %s:%s",
                requestPro.getAppId(), requestPro.getServiceId(),
                requestPro.getMethod(), requestPro.getMethodVersion(), ip, port));

            if (StringUtils.isBlank(requestPro.getDebugServerIp())) {
                //client的debugServerIp是外部无传入，配置取得。
                requestPro.setDebugServerIp(EosClientProp.getDebugServerIp(requestPro.getAppId()));
            }

            ResponsePro responsePro = RpcClientFactory.create(requestPro.getTransporter())
                .doRpc(requestPro, ip, port);
            return responsePro;
        } catch (RpcException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RpcException(RpcException.NETWORK_EXCEPTION, throwable);
        }
    }

    private JSONObject getEosLocation(String appId, String serviceId, String method, String methodVersion) throws RpcException {
        // todo 取得可用服务
        JSONObject jo = ServiceLocation.getInstance().getServiceLocation(appId, serviceId, method, methodVersion);
        if (jo == null) {
            String errTip = "没有找到请求的可用的eos节点,请确保服务" + appId + "-"
                + serviceId + "-"
                + method + "-"
                + methodVersion + "是否有效或者eos节点已经启动！";
            logger.error(errTip);
            throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, errTip);
        }
        return jo;
    }

}

