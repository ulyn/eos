/**
 * @(#)ServiceRequest
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-12 上午11:19
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.filter;

import com.sunsharing.component.utils.base.StringUtils;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.serialize.SerializationFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

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
public class ServiceRequest {

    private Builder builder = null;
    private String[] parameterTypes;

    public ServiceRequest(Builder builder) {
        this.builder = builder;
    }

    public String getTransporter() {
        return this.builder.getTransporter();
    }

    public String[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(String[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public String getAppId() {
        return this.builder.getAppId();
    }

    public String getServiceId() {
        return this.builder.getServiceId();
    }

    public String getServiceVersion() {
        return this.builder.getServiceVersion();
    }

    public String getMock() {
        return this.builder.getMock();
    }

    public String getDebugServerIp() {
        return this.builder.getDebugServerIp();
    }

    public String getMethodName() {
        return this.builder.getMethodName();
    }

    public Object[] getArguments() {
        return this.builder.getArguments();
    }

    public String getRemoteAddr() {
        return this.builder.getRemoteAddr();
    }

    public String getUserAgent() {
        return this.builder.getUserAgent();
    }

    public Map<String, Object> getAttributeMap() {
        return this.builder.getAttributeMap();
    }

    public String getSerialization() {
        return this.builder.getSerialization();
    }

    public int getTimeout() {
        return this.builder.getTimeout();
    }

    public void setTimeout(int timeout) {
        this.builder.setTimeout(timeout);
    }

    public void setAttribute(String key, Object val) {
        this.builder.setAttribute(key, val);
    }

    public Object getAttribute(String key) {
        return this.builder.getAttribute(key);
    }

    public RpcContext createRpcContext() {
        RpcContext rpcContext = new RpcContext();
        rpcContext.setAttributeMap(this.getAttributeMap());
        rpcContext.setUserAgent(this.getUserAgent());
        rpcContext.setRemoteAddr(this.getRemoteAddr());
        return rpcContext;
    }

    public RequestPro toRequestPro() {
        RequestPro requestPro = new RequestPro();
        requestPro.setAppId(this.getAppId());
        requestPro.setServiceId(this.getServiceId());
        requestPro.setServiceVersion(this.getServiceVersion());
        requestPro.setSerialization(this.getSerialization());
        requestPro.setDebugServerIp(this.getDebugServerIp());
        requestPro.setMock(this.getMock());

        RpcContext context = new RpcContext();
        context.setUserAgent(this.getUserAgent());
        context.setRemoteAddr(this.getRemoteAddr());
        context.setAttributeMap(this.getAttributeMap());
        requestPro.setRpcContext(context);

        RpcInvocation rpcInvocation = new RpcInvocation();
        rpcInvocation.setArguments(this.getArguments());
        rpcInvocation.setMethodName(this.getMethodName());
//        rpcInvocation.setMock(this.getMock());
        requestPro.setInvocation(rpcInvocation);

        return requestPro;
    }

    public static ServiceRequest createServiceRequest(RequestPro requestPro) throws IOException, ClassNotFoundException {
        Invocation invocation = requestPro.getInvocation() == null ? requestPro.toInvocation() : requestPro.getInvocation();
        RpcContext rpcContext = requestPro.getRpcContext() == null ? requestPro.toRpcContext() : requestPro.getRpcContext();
        return new Builder().setAppId(requestPro.getAppId())
                .setAppId(requestPro.getAppId())
                .setServiceId(requestPro.getServiceId())
                .setServiceVersion(requestPro.getServiceVersion())
                .setDebugServerIp(requestPro.getDebugServerIp())
                .setSerialization(requestPro.getSerialization())
                .setMock(requestPro.getMock())
                .setRpcContext(rpcContext)
                .setMethodName(invocation.getMethodName())
                .setArguments(invocation.getArguments())
                .build();
    }

    public byte[] serializeToBytes() throws IOException {
        return SerializationFactory.serializeToBytes(this.builder, this.builder.serialization);
    }

    public static ServiceRequest createServiceRequest(byte[] serviceRequestBytes, String serialization) throws IOException, ClassNotFoundException {
        Builder newBuilder = SerializationFactory.deserializeBytes(serviceRequestBytes, Builder.class, serialization);
        return new ServiceRequest(newBuilder);
    }

    public static class Builder implements Serializable {
        /**
         * 基本协议字段 *
         */
        //应用id(32)
        private String appId;
        //服务id(20)
        private String serviceId;
        //服务版本(10)
        private String serviceVersion;
        //模拟取值字段(20)
        private String mock = "";
        //联调ip(20)
        private String debugServerIp;
        /**
         * 基本协议字段 *
         */

        private String methodName;
        //    private String[] parameterTypes;
        private Object[] arguments;
        //    private String mock;
        //上下文
        //请求者地址
        private String remoteAddr = "";
        //userAgent,表明是java调用的还是前端js调用
        private String userAgent = "java_eos_client";
        //额外参数map
        private Map<String, Object> attributeMap = new HashMap<String, Object>();
        protected String serialization = Constants.DEFAULT_SERIALIZATION;
        private int timeout = Constants.DEFAULT_TIMEOUT;
        private String transporter = Constants.DEFAULT_TRANSPORTER;

        public String getAppId() {
            return appId;
        }

        public Builder setAppId(String appId) {
            this.appId = appId;
            return this;
        }

        public String getServiceId() {
            return serviceId;
        }

        public Builder setServiceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public String getServiceVersion() {
            return serviceVersion;
        }

        public Builder setServiceVersion(String serviceVersion) {
            this.serviceVersion = serviceVersion;
            return this;
        }

        public String getMock() {
            return mock;
        }

        public Builder setMock(String mock) {
            this.mock = mock;
            return this;
        }

        public String getDebugServerIp() {
            return debugServerIp;
        }

        public Builder setDebugServerIp(String debugServerIp) {
            this.debugServerIp = debugServerIp;
            return this;
        }

        public String getMethodName() {
            return methodName;
        }

        public Builder setMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }

        public Object[] getArguments() {
            return arguments;
        }

        public Builder setArguments(Object[] arguments) {
            this.arguments = arguments;
            return this;
        }

        public String getRemoteAddr() {
            return remoteAddr;
        }

        public Builder setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
            return this;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Map<String, Object> getAttributeMap() {
            return attributeMap;
        }

        public Builder setAttributeMap(Map<String, Object> attributeMap) {
            this.attributeMap = attributeMap;
            return this;
        }

        public String getSerialization() {
            return serialization;
        }

        public Builder setSerialization(String serialization) {
            if (!StringUtils.isBlank(serialization)) {
                this.serialization = serialization;
            }
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public String getTransporter() {
            return transporter;
        }

        public Builder setTransporter(String transporter) {
            this.transporter = transporter;
            return this;
        }

        public Builder setAttribute(String key, Object val) {
            this.attributeMap.put(key, val);
            return this;
        }

        public Object getAttribute(String key) {
            return this.attributeMap.get(key);
        }

        public Builder setRpcContext(RpcContext rpcContext) {
            this.setRemoteAddr(rpcContext.getRemoteAddr());
            this.setUserAgent(rpcContext.getUserAgent());
            this.setAttributeMap(rpcContext.getAttributeMap());
            return this;
        }

        public ServiceRequest build() {
            return new ServiceRequest(this);
        }
    }
}

