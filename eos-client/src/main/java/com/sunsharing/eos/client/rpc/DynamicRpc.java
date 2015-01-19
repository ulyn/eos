/**
 * @(#)DynamicRpc
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-8 下午6:56
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 动态调用远程服务，是服务调用的基础类
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class DynamicRpc extends RpcInvoker {
    String appId;
    String serviceId;
    String serviceVersion;
    String debugServerIp;
    String serialization;
    String mock;
    RpcContext rpcContext;
    String transporter = Constants.DEFAULT_TRANSPORTER;
    int timeout = Constants.DEFAULT_TIMEOUT;

    public DynamicRpc(String appId, String serviceId, String v) {
        this.setDebugServerIp(SysProp.getDebugServerIp(appId));
        this.appId = appId;
        this.serviceId = serviceId;
        this.serviceVersion = v;
    }
    public static DynamicRpc create(String appId, String serviceId, String v) {
        return new DynamicRpc(appId, serviceId, v);
    }

    public DynamicRpc setDebugServerIp(String debugServerIp) {
        this.debugServerIp = debugServerIp;
        return this;
    }

    public DynamicRpc setSerialization(String serialization) {
        this.serialization = serialization;
        return this;
    }

    public DynamicRpc setMock(String mock) {
        if (SysProp.use_mock) {
            this.mock = mock;
        }
        return this;
    }

    public DynamicRpc setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public DynamicRpc setTransporter(String transporter) {
        this.transporter = transporter;
        return this;
    }

    public DynamicRpc setRpcContext(RpcContext context) {
        //由于context需要serial后才可以进行 所以在调用时候才设置到req
        this.rpcContext = context;
        return this;
    }



    /**
     * 执行调用
     *
     * @param retType
     * @param <T>
     * @return
     * @throws com.sunsharing.eos.common.rpc.RpcException
     */
    public <T> T doInvoke(Class<T> retType, String methodName, Object... args) throws RpcException {
        if (this.rpcContext == null) {
            RpcContext rpcContext = RpcContextContainer.getRpcContext();
            if (rpcContext == null) {
                rpcContext = new RpcContext();
                rpcContext.setUserAgent("eos-client DynamicRpc");
            }
            this.setRpcContext(rpcContext);
        }


        ServiceRequest request = new ServiceRequest.Builder()
                .setAppId(this.appId)
                .setServiceId(this.serviceId)
                .setServiceVersion(this.serviceVersion)
                .setDebugServerIp(this.debugServerIp)
                .setSerialization(this.serialization)
                .setMock(this.mock)
                .setTimeout(this.timeout)
                .setTransporter(this.transporter)
                .setRpcContext(rpcContext)
                .setMethodName(methodName)
                .setArguments(args)
                .build();

        ServiceResponse serviceResponse = new ServiceResponse(request);

        doInvoke(request, serviceResponse);
        try {
            Object o = getResult(request, serviceResponse, retType);
            return (T) o;
        } catch (RpcException e) {
            throw e;
        } catch (Throwable e) {
            throw new RpcException(e.getMessage(), e);
        }
    }


    public static void main(String[] args) {
        ConfigContext.instancesBean(SysProp.class);
        ServiceLocation.getInstance().connect();
        ZookeeperUtils utils = ZookeeperUtils.getInstance();
        Map map = DynamicRpc.create("legend", "appService", "0.5")
//                .serial("")
//                .debugServerIp(SysProp.getDebugServerIp("legend"))
//                .context(RpcContextContainer.getRpcContext())
                .setMock("success")
                .doInvoke(HashMap.class, "getSystemConfig");
        System.out.println(map);
    }
}

