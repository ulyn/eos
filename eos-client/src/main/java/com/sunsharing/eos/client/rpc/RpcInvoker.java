/**
 * @(#)Invoker
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2015
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 15-1-17 下午3:13
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.component.utils.base.StringUtils;
import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.exception.ExceptionHandler;
import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;
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
public class RpcInvoker {

    private static Logger logger = Logger.getLogger(RpcInvoker.class);

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
        byte[] bytes = Base64.decode(serviceReqBase64Str);
        byte[] result = invoke(bytes, serialization);
        return Base64.encode(result);
    }

    /**
     * @param serviceReqBytes
     * @param serialization
     * @return
     * @throws IOException
     */
    public static byte[] invoke(byte[] serviceReqBytes, String serialization) throws IOException {
        if (com.sunsharing.eos.common.utils.StringUtils.isBlank(serialization)) {
            serialization = Constants.DEFAULT_SERIALIZATION;
        }
        ServiceResponse response = new ServiceResponse();
        response.setSerialization(serialization);
        try {
            ServiceRequest request = ServiceRequest.createServiceRequest(serviceReqBytes, serialization);
            RpcInvoker.doInvoke(request, response);
        } catch (RpcException e) {
            logger.error("调用代理RpcInvoker异常！！", e);
            response.writeError(e);
        } catch (Exception e) {
            logger.error("调用代理异常！！", e);
            response.writeError(e);
        }
        return response.serializeToBytes();
    }

    public static void doInvoke(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        FilterChain filterChain =
                FilterManager.createFilterChain(serviceRequest.getAppId(), serviceRequest.getServiceId());
        RpcFilter rpcFilter = new RpcFilter();
        filterChain.addFilter(rpcFilter);
        try {
            filterChain.doFilter(serviceRequest, serviceResponse);
        } catch (Exception e) {
            serviceResponse.writeError(e);
        }
        ExceptionHandler.tryHandleException(serviceRequest, serviceResponse, ServiceContext.getExceptionResolver());
    }

    /**
     * 转换ResponsePro获取返回结果
     *
     * @param serviceRequest
     * @param serviceResponse
     * @param retType
     * @return
     * @throws Throwable
     */
    protected Object getResult(ServiceRequest serviceRequest, ServiceResponse serviceResponse, Class retType) throws RpcException {
        if (serviceResponse.hasException()) {
            throw new RpcException(serviceResponse.getException().getMessage(), serviceResponse.getException());
        }
        boolean isMock = !StringUtils.isBlank(serviceRequest.getMock());

        Object value = serviceResponse.getValue();
        if (isMock) {
            if (void.class == retType || value == null) {
                return null;
            }
            //返回的类型一样，则不需要进行转换，返回类型是string或者不是模拟返回
            //返回的类型不一样，则需要进行转换
//                if (value.getClass() != retType) {
            if (value instanceof String && !retType.isInstance(value)) {
                value = CompatibleTypeUtils.compatibleTypeConvert((String) value, retType);
            }
        }
        return value;
    }

    public static void main(String[] args) throws IOException {
        ServiceRequest serviceRequest = new ServiceRequest.Builder().setSerialization("fastjson").build();
        byte[] bytes = serviceRequest.serializeToBytes();
        System.out.println(new String(bytes));
        System.out.println(Base64.encode(serviceRequest.serializeToBytes()));
        ServiceRequest serviceRequest2 = new ServiceRequest.Builder().setSerialization("hessian").build();
        System.out.println(Base64.encode(serviceRequest2.serializeToBytes()));

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setSerialization("fastjson");
        byte[] responseBytes = serviceRequest.serializeToBytes();
        System.out.println(new String(responseBytes));
        System.out.println(Base64.encode(responseBytes));
        serviceResponse.setSerialization("hessian");
        System.out.println(Base64.encode(serviceResponse.serializeToBytes()));
    }
}

