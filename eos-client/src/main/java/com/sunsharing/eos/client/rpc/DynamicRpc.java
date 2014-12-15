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

import com.alibaba.fastjson.JSONObject;
import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.component.utils.base.StringUtils;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RemoteHelper;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;
import org.apache.log4j.Logger;

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
public class DynamicRpc {

    RequestPro req = null;
    RpcContext rpcContext;
    String transporter = Constants.DEFAULT_TRANSPORTER;
    int timeout = Constants.DEFAULT_TIMEOUT;

    protected DynamicRpc(RequestPro req) {
        req.setDebugServerIp(SysProp.getDebugServerIp(req.getAppId()));
        this.req = req;
    }
    public static DynamicRpc create(String appId, String serviceId, String v) {
        RequestPro req = new RequestPro();
        req.setAppId(appId);
        req.setServiceId(serviceId);
        req.setServiceVersion(v);
        return new DynamicRpc(req);
    }

    public DynamicRpc setDebugServerIp(String debugServerIp) {
        this.req.setDebugServerIp(debugServerIp);
        return this;
    }

    public DynamicRpc setSerialization(String serialization) {
        this.req.setSerialization(serialization);
        return this;
    }

    public DynamicRpc setMock(String mock) {
        if (SysProp.use_mock) {
            this.req.setMock(mock);
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


    public static void doInvoke(ServiceRequest serviceRequest, ServiceResponse serviceResponse) throws RpcException {
        RequestPro requestPro = serviceRequest.getRequestPro();
        FilterChain filterChain =
                FilterManager.createFilterChain(requestPro.getAppId(), requestPro.getServiceId());
        RpcFilter rpcFilter = new RpcFilter();
        filterChain.addFilter(rpcFilter);
        try {
            filterChain.doFilter(serviceRequest, serviceResponse);
        } catch (ServiceFilterException e) {
            throw new RpcException(e.getMessage(), e);
        }
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
        try {
            this.req.setRpcContext(this.rpcContext);
        } catch (Exception e) {
            throw new RpcException("设置RpcContext异常:" + e.getMessage(), e);
        }
        RpcInvocation invocation = new RpcInvocation();
        invocation.setMethodName(methodName);
        invocation.setArguments(args);
        try {
            this.req.setInvocation(invocation);
        } catch (Exception e) {
            throw new RpcException("设置RpcInvocation异常:" + e.getMessage(), e);
        }
        ServiceRequest request = new ServiceRequest(req, transporter, timeout);

        ResponsePro responsePro = new ResponsePro();
        responsePro.setSerialization(this.req.getSerialization());
        ServiceResponse serviceResponse = new ServiceResponse(responsePro);
        doInvoke(request, serviceResponse);
        try {
            Object o = getResult(req, retType, serviceResponse.getResponsePro());
            return (T) o;
        } catch (RpcException e) {
            throw e;
        } catch (Throwable e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

    /**
     * 转换ResponsePro获取返回结果
     *
     * @param requestPro
     * @param retType
     * @param responsePro
     * @return
     * @throws Throwable
     */
    private Object getResult(RequestPro requestPro, Class retType, ResponsePro responsePro) throws Throwable {
        boolean isMock = !StringUtils.isBlank(requestPro.getMock());
        Result rpcResult = responsePro.toResult();
        if (responsePro.getStatus() == Constants.STATUS_ERROR) {
            if (rpcResult.hasException()) {
                throw rpcResult.getException();
            } else {
                String error = "服务调用失败！"
                        + requestPro.getAppId() + "-"
                        + requestPro.getServiceId() + "-"
                        + requestPro.getServiceVersion();
                throw new RpcException(RpcException.UNKNOWN_EXCEPTION, error);
            }
        } else {
            Object value = rpcResult.getValue();
            if (isMock) {
                if (void.class == retType || value == null) {
                    return null;
                }
                //返回的类型一样，则不需要进行转换，返回类型是string或者不是模拟返回
                //返回的类型不一样，则需要进行转换
//                if (value.getClass() != retType) {
                if (value instanceof String && !retType.isInstance(value)) {
                    value = CompatibleTypeUtils.compatibleTypeConvert((String) value, retType);
                }
            }
            return value;
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

