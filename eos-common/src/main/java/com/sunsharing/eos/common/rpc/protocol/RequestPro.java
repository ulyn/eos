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

import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
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
    //服务id(20)
    protected String serviceId;
    //服务版本(10)
    protected String serviceVersion;
    //模拟取值字段(20)
    protected String mock = "";
    //联调ip(20)
    protected String debugServerIp;
    //请求的上下文消息长度(4)
    protected int rpcContextLength;
    //请求的上下文消息(rpcContextLength)
    protected byte[] rpcContextBytes;
    //请求参数对象序列化的字节(bodyLength)
    protected byte[] invocationBytes;

    private RpcContext rpcContext;
    private Invocation invocation;

    public RpcContext getRpcContext() {
        return rpcContext;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getDebugServerIp() {
        return debugServerIp;
    }

    public void setDebugServerIp(String debugServerIp) {
        this.debugServerIp = debugServerIp;
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public byte[] getInvocationBytes() {
        return invocationBytes;
    }

    public void setInvocationBytes(byte[] invocationBytes) {
        this.invocationBytes = invocationBytes;
    }

    public byte[] getRpcContextBytes() {
        return rpcContextBytes;
    }

    public void setRpcContextBytes(byte[] rpcContextBytes) {
        this.rpcContextBytes = rpcContextBytes;
    }


    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    /**
     * 将invocation字节序列化为对象
     *
     * @return
     * @throws Exception
     */
    public Invocation toInvocation() throws IOException, ClassNotFoundException {
        return SerializationFactory.deserializeBytes(invocationBytes, RpcInvocation.class, this.getSerialization());
    }


    public void setRpcContext(RpcContext rpcContext) {
        this.rpcContext = rpcContext;
    }

    /**
     * 将rpcContext字节序列化为对象
     *
     * @return
     * @throws Exception
     */
    public RpcContext toRpcContext() throws IOException, ClassNotFoundException {
        return SerializationFactory.deserializeBytes(rpcContextBytes, RpcContext.class, this.getSerialization());
    }

    @Override
    protected int getRealBodyLength() {
        if (invocationBytes == null && invocation == null) {
            return 0;
        } else {
            if (invocationBytes == null) {
                try {
                    setInvocationBytes(SerializationFactory.serializeToBytes(invocation, this.getSerialization()));
                } catch (IOException e) {
                    throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, "序列化参数invocation异常:" + invocation, e);
                }
            }
            return invocationBytes.length;
        }
    }

    @Override
    public ChannelBuffer generate() {
        setAction(REQUEST_MSG);

        byte[] subHeader = new byte[102];
        StringUtils.putString(subHeader, appId, 0);
        StringUtils.putString(subHeader, serviceId, 32);
        StringUtils.putString(subHeader, serviceVersion, 52);
        StringUtils.putString(subHeader, mock, 62);
        StringUtils.putString(subHeader, debugServerIp, 82);

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes(getHeaderBytes());
        buffer.writeBytes(subHeader);
        //写入rpcContext
        if (rpcContextBytes == null) {
            try {
                setRpcContextBytes(SerializationFactory.serializeToBytes(rpcContext, this.getSerialization()));
            } catch (IOException e) {
                throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, "序列化参数rpcContext异常:" + invocation, e);
            }
        }
        buffer.writeBytes(StringUtils.intToBytes(rpcContextBytes.length));
        buffer.writeBytes(rpcContextBytes);

        buffer.writeBytes(invocationBytes);
        return buffer;
    }

    @Override
    public BaseProtocol createFromChannel(ChannelBuffer buffer) {
        if (buffer.readableBytes() < 52 + 106) {
            //多保证四个字节是rpcContextLength，所以102改为106
            return null;
        }
        buffer.markReaderIndex();

        RequestPro pro = new RequestPro();
        setHeader(pro, buffer);

        pro.appId = readString(32, buffer);
        pro.serviceId = readString(20, buffer);
        pro.serviceVersion = readString(10, buffer);
        pro.mock = readString(20, buffer);
        pro.debugServerIp = readString(20, buffer);
        pro.rpcContextLength = buffer.readInt();

        if (buffer.readableBytes() < pro.bodyLength + pro.rpcContextLength) {
            buffer.resetReaderIndex();
            return null;
        }
        byte[] rpcContextBytes = new byte[pro.rpcContextLength];
        buffer.readBytes(rpcContextBytes);
        pro.setRpcContextBytes(rpcContextBytes);

        byte[] bodyBytes = new byte[pro.bodyLength];
        buffer.readBytes(bodyBytes);
        pro.setInvocationBytes(bodyBytes);
        return pro;
    }
}

