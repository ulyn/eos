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

import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.SerializationFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
public class ServiceResponse {

    ResponsePro responsePro;
    Class retType = null;

    public ServiceResponse(String serialization) {
        this.responsePro = new ResponsePro();
        this.responsePro.setSerialization(serialization);
    }

    public ServiceResponse(ResponsePro responsePro) {
        this.responsePro = responsePro;
    }

    public void write(ResponsePro responsePro) {
        this.responsePro = responsePro;
    }

    public void write(Object o) {
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

    public Class getRetType() {
        return retType;
    }

}

