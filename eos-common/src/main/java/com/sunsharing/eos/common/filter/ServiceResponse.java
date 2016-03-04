/**
 * @(#)ServiceResponse
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
import com.sunsharing.eos.common.annotation.Version;
import com.sunsharing.eos.common.rpc.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.BaseProtocol;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.VersionUtil;
import org.jboss.netty.buffer.ChannelBuffers;

import java.io.IOException;
import java.io.Serializable;

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
public class ServiceResponse implements Serializable {

    private String msgId;
    private String eosVersion;
    private String serialization = Constants.DEFAULT_SERIALIZATION;

    private Object value;
    private Throwable exception;

    public ServiceResponse() {
        this.eosVersion = VersionUtil.getVersion();
    }

    public ServiceResponse(String msgId,String serialization) {
        this.eosVersion = VersionUtil.getVersion();
        this.serialization = serialization;
        this.msgId = msgId;
    }

    public ServiceResponse(ServiceRequest request) {
        this.serialization = request.getSerialization();
        this.msgId = request.getMsgId();
        this.eosVersion = VersionUtil.getVersion();
    }

    public String getMsgId() {
        return msgId;
    }

    private void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getEosVersion() {
        return eosVersion;
    }

    private void setEosVersion(String eosVersion) {
        this.eosVersion = eosVersion;
    }

    public String getSerialization() {
        return serialization;
    }

    private void setSerialization(String serialization) {
        if (!StringUtils.isBlank(serialization)) {
            this.serialization = serialization;
        }
    }

    public Object getValue() {
        return value;
    }

    public Throwable getException() {
        return exception;
    }

    public boolean hasException() {
        return exception != null;
    }

    public void writeValue(Object o) {
        this.value = o;
        this.exception = null;
    }

    public void writeError(Throwable ex) {
        this.exception = ex;
    }

    public ResponsePro toResponsePro() {
        ResponsePro responsePro = new ResponsePro();
        responsePro.setAction(BaseProtocol.REQUEST_MSG_RESULT);
        responsePro.setSerialization(this.serialization);
        responsePro.setMsgId(this.getMsgId());
        responsePro.setEosVersion(this.getEosVersion());
        RpcResult result = new RpcResult(this.getValue());
        if (this.hasException()) {
            result.setException(this.exception);
            responsePro.setStatus(Constants.STATUS_ERROR);
        }
        try {
            responsePro.setResultBytes(
                    SerializationFactory.serializeToBytes(result, this.getSerialization()));
        } catch (IOException e) {
            throw new RuntimeException("["+ this.getSerialization()
                    +"]序列化RpcResult对象异常：" + e.getMessage(),e);
        }
        return responsePro;
    }

    /**
     * 复制对象
     * @param response
     */
    public void copyForm(ServiceResponse response){
        this.setSerialization(response.getSerialization());
        this.setEosVersion(response.getEosVersion());
        this.setMsgId(response.getMsgId());
        this.writeValue(response.getValue());
        this.writeError(response.getException());
    }

    public byte[] toBytes() throws IOException {
        ResponsePro responsePro = this.toResponsePro();
        return responsePro.generate().array();
    }

    public static ServiceResponse formBytes(byte[] serviceResponseBytes) throws IOException, ClassNotFoundException {
        ResponsePro responsePro = new ResponsePro();
        responsePro.createFromChannel(ChannelBuffers.wrappedBuffer(serviceResponseBytes));
        return createServiceResponse(responsePro);
    }

    public static ServiceResponse createServiceResponse(ResponsePro responsePro) throws IOException, ClassNotFoundException {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setMsgId(responsePro.getMsgId());
        serviceResponse.setSerialization(responsePro.getSerialization());
        serviceResponse.setEosVersion(responsePro.getEosVersion());

        RpcResult result = toResult(responsePro);
        if(result != null){
            if(result.hasException()){
                serviceResponse.writeError(result.getException());
            }else{
                serviceResponse.writeValue(result.getValue());
            }
        }
        return serviceResponse;
    }


    public static RpcResult toResult(ResponsePro responsePro) throws IOException, ClassNotFoundException {
        if (responsePro.getResultBytes() == null || responsePro.getResultBytes().length == 0) {
            return null;
        }
        RpcResult result = SerializationFactory.deserializeBytes(responsePro.getResultBytes(),
                RpcResult.class, responsePro.getSerialization());
        return result;
    }


    public static void main(String[] args) {
//        ServiceResponse response = new ServiceResponse(new ResponsePro());
//        Result result = new RpcResult();
//        response.writeResult(result);
//        response.writeResult(new RpcResult());
//        response.writeResponsePro(new ResponsePro());
//        response.writeValue("");

    }
}

