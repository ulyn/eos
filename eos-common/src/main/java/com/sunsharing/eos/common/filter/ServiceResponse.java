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
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.serialize.SerializationFactory;

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

    private Object value;
    private Throwable exception;
    private String serialization = Constants.DEFAULT_SERIALIZATION;

    public ServiceResponse() {
    }

    public ServiceResponse(ServiceRequest request) {
        this.serialization = request.getSerialization();
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
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
        if (this.hasException()) {
            responsePro.setExceptionResult(this.exception);
        } else {
            responsePro.setSerialization(this.serialization);
            RpcResult result = new RpcResult(this.getValue());
            responsePro.setResult(result);
        }
        return responsePro;
    }

    public byte[] serializeToBytes() throws IOException {
        return SerializationFactory.serializeToBytes(this, this.serialization);
    }

    public static ServiceResponse createServiceResponse(byte[] serviceResponseBytes, String serialization) throws IOException, ClassNotFoundException {
        ServiceResponse serviceResponse = SerializationFactory.deserializeBytes(serviceResponseBytes, ServiceResponse.class, serialization);
        return serviceResponse;
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

