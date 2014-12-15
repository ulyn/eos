/**
 * @(#)WebServiceProxy
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-12 下午5:38
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.clientexample.test;

import com.sunsharing.eos.client.rpc.ProxyFilter;
import com.sunsharing.eos.common.filter.ServiceFilterException;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
import com.sunsharing.eos.common.serialize.SerializationFactory;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> ws代理的过滤器,将所有的请求代理走
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class WebServiceProxyFilter extends ProxyFilter {

    @Override
    public void process(ServiceRequest req, ServiceResponse res) throws ServiceFilterException {
        try {
            String serialization = req.getRequestPro().getSerialization();
            String base = SerializationFactory.serializeToBase64Str(req, serialization);
            String resutlstr = (String) CallWs.send("http://192.168.0.60:8095/services/eosProxy?wsdl", "invoke", new Object[]{base, serialization});
            ServiceResponse response = SerializationFactory.deserializeBase64Str(resutlstr, ServiceResponse.class, serialization);
            res.writeResponsePro(response.getResponsePro());
        } catch (Exception e) {
            throw new ServiceFilterException(e.getMessage(), e);
        }
    }
}

