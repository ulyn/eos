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

import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.rpc.ClientProxy;
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RemoteHelper;
import com.sunsharing.eos.common.utils.StringUtils;

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
        //zookeeper取得服务的ip
        JSONObject jo;
        if (StringUtils.isBlank(config.getMock())) {
            jo = ServiceLocation.getInstance().getServiceLocation(config.getAppId(), config.getId(), config.getVersion());
        } else {
            jo = ServiceLocation.getInstance().getOnlineEOS();
        }
        if (jo == null) {
            if (StringUtils.isBlank(config.getMock())) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有找到请求的可用的eos节点！");
            }
            throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "对于模拟调用，没有找到可用的eos！");
        }

        String ip = jo.getString("ip");
        int port = jo.getInteger("port");

        RequestPro pro = new RequestPro();
        pro.setAppId(config.getAppId());
        pro.setServiceId(config.getId());
        pro.setServiceVersion(config.getVersion());
        pro.setSerialization(config.getSerialization());
        pro.setMock(config.getMock());
        pro.setInvocation(invocation);
        pro.setDebugServerIp(SysProp.debugServerIp);

        RemoteHelper helper = new RemoteHelper();
        ResponsePro responsePro = helper.call(pro, ip, port, config.getTransporter(), config.getTimeout());
        Result rpcResult = responsePro.toResult();

        if (rpcResult.hasException()) {
            throw rpcResult.getException();
        } else return rpcResult.getValue();
    }
}

