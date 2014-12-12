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

import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.SerializationFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    private RequestPro requestPro;
    private String transporter;
    private int timeout = Constants.DEFAULT_TIMEOUT;
    private Map<String, Object> attributeMap = new HashMap<String, Object>();

    public ServiceRequest(RequestPro requestPro, String transporter) {
        this.requestPro = requestPro;
        this.transporter = transporter;
    }

    public ServiceRequest(RequestPro requestPro, String transporter, int timeout) {
        this.requestPro = requestPro;
        this.transporter = transporter;
        this.timeout = timeout;
    }

    public RequestPro getRequestPro() {
        return requestPro;
    }

    public String getTransporter() {
        return transporter;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setAttribute(String key, Object val) {
        this.attributeMap.put(key, val);
    }

    public Object getAttribute(String key) {
        return this.attributeMap.get(key);
    }

    public Map<String, Object> getAttributeMap() {
        return attributeMap;
    }

}

