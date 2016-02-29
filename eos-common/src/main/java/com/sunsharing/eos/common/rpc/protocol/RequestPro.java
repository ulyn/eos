/**
 * @(#)RequestPro
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-4 下午11:53
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.protocol;

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;


/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 客户端请求eos的协议
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class RequestPro extends BaseProtocol {

    //应用id(32)
    protected String appId;
    //服务id(30)
    protected String serviceId;
    //方法名(30)
    protected String method;
    //服务方法版本(5)
    protected String methodVersion;
    //调用超时时间,毫秒(4)
    protected int timeout = Constants.DEFAULT_TIMEOUT;
    //联调ip(20)
    protected String debugServerIp;
    //传输通道(10)
    protected String transporter;
    //请求的上下文消息长度(4)
//    private int rpcContextLength;
    //请求的上下文消息(rpcContextLength)
    protected byte[] rpcContextBytes;
    //请求参数对象序列化的字节长度(4)
//    private int parameterLength;
    //请求参数对象序列化的字节(parameterLength)
    protected byte[] parameterMapBytes;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethodVersion() {
        return methodVersion;
    }

    public void setMethodVersion(String methodVersion) {
        this.methodVersion = methodVersion;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getDebugServerIp() {
        return debugServerIp;
    }

    public void setDebugServerIp(String debugServerIp) {
        this.debugServerIp = debugServerIp;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public byte[] getRpcContextBytes() {
        return rpcContextBytes;
    }

    public void setRpcContextBytes(byte[] rpcContextBytes) {
        this.rpcContextBytes = rpcContextBytes;
    }

    public byte[] getParameterMapBytes() {
        return parameterMapBytes;
    }

    public void setParameterMapBytes(byte[] parameterMapBytes) {
        this.parameterMapBytes = parameterMapBytes;
    }

    @Override
    public ChannelBuffer generate() {
        setAction(REQUEST_MSG);

        byte[] subHeader = new byte[131];
        StringUtils.putString(subHeader, appId, 0);
        StringUtils.putString(subHeader, serviceId, 32);
        StringUtils.putString(subHeader, method, 62);
        StringUtils.putString(subHeader, methodVersion, 92);
        StringUtils.putInt(subHeader, timeout, 97);
        StringUtils.putString(subHeader, debugServerIp, 101);
        StringUtils.putString(subHeader, transporter, 121);

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes(getHeaderBytes());
        buffer.writeBytes(subHeader);
        //写入rpcContext，invocation
        buffer.writeBytes(StringUtils.intToBytes(rpcContextBytes.length));
        buffer.writeBytes(StringUtils.intToBytes(parameterMapBytes.length));

        buffer.writeBytes(rpcContextBytes);
        buffer.writeBytes(parameterMapBytes);
        return buffer;
    }

    @Override
    public BaseProtocol createFromChannel(ChannelBuffer buffer) {
        if (buffer.readableBytes() < 187) {
            //  48 + 131 + 8
            //多保证8个字节是rpcContextLength 和 parameterLength
            return null;
        }
        buffer.markReaderIndex();

        RequestPro pro = new RequestPro();
        setHeader(pro, buffer);

        pro.appId = readString(32, buffer);
        pro.serviceId = readString(30, buffer);
        pro.method = readString(30, buffer);
        pro.methodVersion = readString(5, buffer);
        pro.timeout = buffer.readInt();
        pro.debugServerIp = readString(20, buffer);
        pro.transporter = readString(10, buffer);
        int rpcContextLength = buffer.readInt();
        int parameterLength = buffer.readInt();

        if (buffer.readableBytes() < parameterLength + rpcContextLength) {
            buffer.resetReaderIndex();
            return null;
        }
        byte[] rpcContextBytes = new byte[rpcContextLength];
        buffer.readBytes(rpcContextBytes);
        pro.setRpcContextBytes(rpcContextBytes);

        byte[] bodyBytes = new byte[parameterLength];
        buffer.readBytes(bodyBytes);
        pro.setParameterMapBytes(bodyBytes);
        return pro;
    }

}

