/**
 * @(#)WebService
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-12 下午5:44
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.clientproxy.ws;

import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.clientproxy.ProxyInvoke;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;

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
public class WebServiceProxy {
    private Logger logger = Logger.getLogger(WebServiceProxy.class);

    /**
     * @param serviceReqBase64Str ServiceRequest对象的base64字符串
     * @param serialization       ServiceRequest串的序列化方式，当为空时，默认为Constants.DEFAULT_SERIALIZATION
     * @return
     */
    public String invoke(String serviceReqBase64Str, String serialization) throws IOException {
        return ProxyInvoke.invoke(serviceReqBase64Str, serialization);
    }
}

