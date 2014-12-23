/**
 * @(#)ProxyInvoke
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-16 上午11:08
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.clientproxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
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
public class ProxyInvoke {
    private static Logger logger = Logger.getLogger(ProxyInvoke.class);

    /**
     * 代理执行，返回序列化串
     *
     * @param serviceReqBase64Str ServiceRequest对象的base64字符串
     * @param serialization       ServiceRequest串的序列化方式，当为空时，默认为Constants.DEFAULT_SERIALIZATION
     * @return
     */
    public static String invoke(String serviceReqBase64Str, String serialization) throws IOException {
//        logger.info("serviceReqBase64Str="+serviceReqBase64Str);
//        logger.info("serialization=" + serialization);
        if (StringUtils.isBlank(serialization)) {
            serialization = Constants.DEFAULT_SERIALIZATION;
        }
        ServiceResponse response = new ServiceResponse(serialization);
        try {
            ServiceRequest request = SerializationFactory.deserializeBase64Str(serviceReqBase64Str, ServiceRequest.class, serialization);
            DynamicRpc.doInvoke(request, response);
        } catch (RpcException e) {
            logger.error("调用代理DynamicRpc异常！！", e);
            response.writeError(e);
        } catch (Exception e) {
            logger.error("调用代理异常！！", e);
            response.writeError(e);
        } finally {
            return SerializationFactory.serializeToBase64Str(response, serialization);
        }
    }

    /**
     * @param serviceReqBytes
     * @param serialization
     * @return
     * @throws IOException
     */
    public static byte[] invoke(byte[] serviceReqBytes, String serialization) throws IOException {
        if (StringUtils.isBlank(serialization)) {
            serialization = Constants.DEFAULT_SERIALIZATION;
        }
        ServiceResponse response = new ServiceResponse(serialization);
        try {
            ServiceRequest request = SerializationFactory.deserializeBytes(serviceReqBytes, ServiceRequest.class, serialization);
            DynamicRpc.doInvoke(request, response);
        } catch (RpcException e) {
            logger.error("调用代理DynamicRpc异常！！", e);
            response.writeError(e);
        } catch (Exception e) {
            logger.error("调用代理异常！！", e);
            response.writeError(e);
        } finally {
            return SerializationFactory.serializeToBytes(response, serialization);
        }
    }

    public static void main(String[] args) throws IOException {
        ServiceRequest re = new ServiceRequest(new RequestPro(), "fastjson");
        System.out.println(JSON.toJSONString(re, SerializerFeature.WriteMapNullValue));
        System.out.println(SerializationFactory.serializeToBase64Str(re, "fastjson"));
    }
}

