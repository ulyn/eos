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

import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;

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

    ResponsePro responsePro;
//    Class retType = null;

    public ServiceResponse(String serialization) {
        this.responsePro = new ResponsePro();
        this.responsePro.setSerialization(serialization);
    }

    public ServiceResponse(ResponsePro responsePro) {
        this.responsePro = responsePro;
    }

    public void writeResponsePro(ResponsePro responsePro) {
        this.responsePro = responsePro;
    }

    public void writeResult(Result result) {
        this.responsePro.setResult(result);
    }

    public void writeValue(Object o) {
        RpcResult result = new RpcResult(o);
        this.responsePro.setResult(result);
    }

    public void writeError(Throwable ex) {
        RpcResult result = new RpcResult(ex);
        this.responsePro.setResult(result);
    }

    public ResponsePro getResponsePro() {
        return responsePro;
    }

//    public Class getRetType() {
//        return retType;
//    }

    public static void main(String[] args) {
        ServiceResponse response = new ServiceResponse(new ResponsePro());
        Result result = new RpcResult();
        response.writeResult(result);
        response.writeResult(new RpcResult());
        response.writeResponsePro(new ResponsePro());
        response.writeValue("");

    }
}

