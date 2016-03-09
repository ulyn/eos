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
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.filter.AbstractServiceFilter;
import com.sunsharing.eos.common.filter.FilterChain;
import com.sunsharing.eos.common.filter.ServiceFilterException;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.remoting.RpcClientFactory;
import org.apache.log4j.Logger;

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
        //zookeeper取得服务的ip
        JSONObject jo = getEosLocation(serviceRequest.getAppId(), serviceRequest.getServiceId(),
                serviceRequest.getMethod(), serviceRequest.getMethodVersion());
        String ip = jo.getString("ip");
        int port = jo.getInteger("port");

        try {
            logger.debug(String.format("request %s-%s-%s-%s target eos %s:%s",
                    serviceRequest.getAppId(), serviceRequest.getServiceId(),
                    serviceRequest.getMethod(),serviceRequest.getMethodVersion(), ip, port));
            ServiceResponse response = RpcClientFactory.create(serviceRequest.getTransporter())
                    .doRpc(serviceRequest, ip, port);
            serviceResponse.copyForm(response);
        } catch (RpcException e) {
            throw e;
        } catch (Throwable throwable) {
            throw new RpcException(RpcException.NETWORK_EXCEPTION, throwable);
        }
        fc.doFilter(serviceRequest, serviceResponse);
    }

    private JSONObject getEosLocation(String appId, String serviceId, String method, String methodVersion) throws RpcException {
        // todo 取得可用服务
        JSONObject jo = jo = ServiceLocation.getInstance().getServiceLocation(appId, serviceId,method, methodVersion);
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

