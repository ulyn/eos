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

import com.alibaba.fastjson.JSON;
import com.google.common.base.Preconditions;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
import com.sunsharing.eos.common.utils.VersionUtil;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.protocol.BaseProtocol;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
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

    /**
     * 基本协议字段 *
     */
    //消息id(32)
    private String msgId;
    //EOS版本号(5)
    protected String eosVersion;
    //序列化方式(10)
    private String serialization;

    //应用id(32)
    private String appId;
    //服务id(30)
    private String serviceId;
    //方法名(30)
    private String method;
    //服务方法版本(5)
    private String methodVersion;
    //调用超时时间,毫秒(4)
    private int timeout;
    //联调ip(20)
    private String debugServerIp;

    //执行参数
    private Map<String,Object> parameterMap;

    //上下文
    //请求者地址
    private String remoteAddr;
    //userAgent,表明是java调用的还是前端js调用
    private String userAgent;
    //额外参数map
    private Map<String, Object> attributeMap;

    private String transporter;

    private ServiceRequest(Builder builder) {
        this.msgId = builder.msgId;
        this.eosVersion = builder.eosVersion;
        this.serialization = builder.serialization;
        this.appId = builder.appId;
        this.serviceId = builder.serviceId;
        this.method = builder.method;
        this.methodVersion = builder.methodVersion;
        this.timeout = builder.timeout;
        this.debugServerIp = builder.debugServerIp;

        this.parameterMap = builder.parameterMap;

        this.remoteAddr = builder.remoteAddr;
        this.userAgent = builder.userAgent;
        this.attributeMap = builder.attributeMap;

        this.transporter = builder.transporter;
    }

    public String getMsgId() {
        return msgId;
    }

    public String getEosVersion() {
        return eosVersion;
    }

    public String getSerialization() {
        return serialization;
    }

    public String getAppId() {
        return appId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getMethod() {
        return method;
    }

    public String getMethodVersion() {
        return methodVersion;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getDebugServerIp() {
        return debugServerIp;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setAttribute(String key,Object attr) {
        this.attributeMap.put(key,attr);
    }

    /**
     * 取得 <T> 类型的参数
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getParameter(String key,Class<T> clazz){
        Object o = parameterMap.get(key);
        return CompatibleTypeUtils.expectConvert(o,clazz);
    }

    public Map<String, Object> getParameterMap() {
        return Collections.unmodifiableMap(parameterMap);
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
        requestPro.setAction(BaseProtocol.REQUEST_MSG);
        requestPro.setEosVersion(this.getEosVersion());
        requestPro.setMsgId(this.getMsgId());
        requestPro.setSerialization(this.getSerialization());
        requestPro.setAppId(this.getAppId());
        requestPro.setServiceId(this.getServiceId());
        requestPro.setMethod(this.getMethod());
        requestPro.setMethodVersion(this.getMethodVersion());
        requestPro.setTimeout(this.getTimeout());
        requestPro.setDebugServerIp(this.getDebugServerIp());
        requestPro.setTransporter(this.getTransporter());

        RpcContext context = new RpcContext();
        context.setUserAgent(this.getUserAgent());
        context.setRemoteAddr(this.getRemoteAddr());
        context.setAttributeMap(this.getAttributeMap());
        try {
            requestPro.setRpcContextBytes(
                    SerializationFactory.serializeToBytes(context, this.getSerialization()));
        } catch (IOException e) {
            throw new RuntimeException("["+ this.getSerialization()
                    +"]序列化上下文对象异常：" + e.getMessage(),e);
        }
        try {
            requestPro.setParameterMapBytes(
                    SerializationFactory.serializeToBytes(this.getParameterMap(), this.getSerialization()));
        } catch (IOException e) {
            throw new RuntimeException("["+ this.getSerialization()
                    +"]序列化调用入参异常：" + e.getMessage(),e);
        }

        return requestPro;
    }

    public static ServiceRequest createServiceRequest(RequestPro requestPro) throws IOException, ClassNotFoundException {
        RpcContext rpcContext = toRpcContext(requestPro);
        Map<String,Object> parameterMap = toParameterMap(requestPro);
        return new Builder(requestPro.getAppId(),requestPro.getServiceId(),requestPro.getMethod(),requestPro.getMethodVersion())
                .setMsgId(requestPro.getMsgId())
                .setEosVersion(requestPro.getEosVersion())
                .setSerialization(requestPro.getSerialization())
                .setTimeout(requestPro.getTimeout())
                .setTransporter(requestPro.getTransporter())
                .setDebugServerIp(requestPro.getDebugServerIp())
                .setUserAgent(rpcContext.getUserAgent())
                .setRemoteAddr(rpcContext.getRemoteAddr())
                .setAttributeMap(rpcContext.getAttributeMap())
                .setParameterMap(parameterMap)
                .build();
    }


    /**
     * 将rpcContext字节序列化为对象
     *
     * @return
     * @throws Exception
     */
    private static RpcContext toRpcContext(RequestPro requestPro) throws IOException, ClassNotFoundException {
        return SerializationFactory.deserializeBytes(requestPro.getRpcContextBytes(),
                RpcContext.class, requestPro.getSerialization());
    }

    /**
     * 将入参对象字节序列化为对象
     *
     * @return
     * @throws Exception
     */
    private static Map<String,Object> toParameterMap(RequestPro requestPro) throws IOException, ClassNotFoundException {
        return SerializationFactory.deserializeBytes(requestPro.getParameterMapBytes(),
                HashMap.class, requestPro.getSerialization());
    }

    public byte[] toBytes() throws IOException {
        RequestPro requestPro = this.toRequestPro();
        return requestPro.generate().array();
    }

    public static ServiceRequest formBytes(byte[] serviceRequestBytes) throws IOException, ClassNotFoundException {
        RequestPro requestPro = new RequestPro();
        requestPro.createFromChannel(ChannelBuffers.wrappedBuffer(serviceRequestBytes));
        return createServiceRequest(requestPro);
    }

    public static class Builder implements Serializable {
        /**
         * 基本协议字段 *
         */
        //消息id(32)
        private String msgId = com.sunsharing.eos.common.utils.StringUtils.genUUID();
        //EOS版本号(5)
        protected String eosVersion = VersionUtil.getVersion();
        //序列化方式(10)
        private String serialization = Constants.DEFAULT_SERIALIZATION;

        //应用id(32)
        private String appId;
        //服务id(30)
        private String serviceId;
        //方法名(30)
        private String method;
        //服务方法版本(5)
        private String methodVersion;
        //调用超时时间,毫秒(4)
        private int timeout = Constants.DEFAULT_TIMEOUT;
        //联调ip(20)
        private String debugServerIp;

        //执行参数
        private Map<String,Object> parameterMap = new HashMap<String, Object>();

        //上下文
        //请求者地址
        private String remoteAddr = "";
        //userAgent,表明是java调用的还是前端js调用
        private String userAgent = "java_eos_client";
        //额外参数map
        private Map<String, Object> attributeMap = new HashMap<String, Object>();

        private String transporter = Constants.DEFAULT_TRANSPORTER;

        public Builder(String appId, String serviceId, String method, String methodVersion) {
            this.appId = appId;
            this.serviceId = serviceId;
            this.method = method;
            this.methodVersion = methodVersion;
        }

        /**
         * 调用者不需要设置
         * @param
         * @return
         */
        public Builder setMsgId(String msgId) {
            this.msgId = msgId;
            return this;
        }

        /**
         * 调用者不需要设置
         * @param eosVersion
         * @return
         */
        public Builder setEosVersion(String eosVersion) {
            if(eosVersion != null){
                this.eosVersion = eosVersion;
            }
            return this;
        }

        public Builder setSerialization(String serialization) {
            if(serialization != null){
                this.serialization = serialization;
            }
            return this;
        }

        public Builder setTimeout(int timeout) {
            if(timeout > 0){
                this.timeout = timeout;
            }
            return this;
        }

        public Builder setDebugServerIp(String debugServerIp) {
            this.debugServerIp = debugServerIp;
            return this;
        }

        public Builder setRemoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
            return this;
        }

        public Builder setUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public Builder setAttributeMap(Map<String, Object> attributeMap) {
            this.attributeMap = attributeMap;
            return this;
        }

        public Builder setTransporter(String transporter) {
            if(transporter != null){
                this.transporter = transporter;
            }
            return this;
        }

        public Builder setParameterMap(Map<String,Object> parameterMap) {
            this.parameterMap = parameterMap;
            return this;
        }

        public Builder setParameter(String parameterName,Object value) {
            if(this.parameterMap == null){
                this.parameterMap = new HashMap<String, Object>();
            }
            this.parameterMap.put(parameterName, value);
            return this;
        }

        private Builder setRpcContextAttr(){
            RpcContext context = RpcContextContainer.getRpcContext();
            if(context != null){
                this.setRemoteAddr(context.getRemoteAddr());
                this.setUserAgent(context.getUserAgent());
                this.setAttributeMap(context.getAttributeMap());
            }
            return this;
        }

        public ServiceRequest build() {
            setRpcContextAttr();
            return new ServiceRequest(this);
        }
    }
}

