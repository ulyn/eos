/**
 * @(#)DynamicRpc
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-8 下午6:56
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.common.exception.ExceptionHandler;
import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;

import java.io.IOException;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 动态调用远程服务，是服务调用的基础类
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class DynamicRpc{


    public static void invoke(ServiceRequest request) throws RpcException {
        invoke(request,void.class);
    }

    /**
     * 执行调用
     *
     * @param retType
     * @param <T>
     * @return
     * @throws com.sunsharing.eos.common.rpc.RpcException
     */
    public static <T> T invoke(ServiceRequest request,Class<T> retType) throws RpcException {
        ServiceResponse serviceResponse = new ServiceResponse(request);

        invoke(request, serviceResponse);
        try {
            return getResult(request, serviceResponse, retType);
        } catch (RpcException e) {
            throw e;
        } catch (Throwable e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

    public static void invoke(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        FilterChain filterChain =
                FilterManager.createFilterChain(serviceRequest.getAppId(), serviceRequest.getServiceId());
        RpcCaller caller = new RpcCaller();
        filterChain.addFilter(caller);
        try {
            filterChain.doFilter(serviceRequest, serviceResponse);
        } catch (Exception e) {
            serviceResponse.writeError(e);
        }
        ExceptionHandler.tryHandleException(serviceRequest, serviceResponse,
                ServiceContext.getInstance().getExceptionResolver());
    }

    /**
     * @param serviceReqBase64Str ServiceRequest对象的base64字符串
     * @return
     */
    public static String invoke(String serviceReqBase64Str) throws RpcException {
        ServiceRequest request = null;
        try {
            request = ServiceRequest.formBytes(Base64.decode(serviceReqBase64Str));
        } catch (IOException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION,e);
        } catch (ClassNotFoundException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION,e);
        }
        ServiceResponse response = new ServiceResponse(request);
        invoke(request, response);
        try {
            return Base64.encode(response.toBytes());
        } catch (IOException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION,e);
        }
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
    protected static <T> T getResult(ServiceRequest serviceRequest, ServiceResponse serviceResponse, Class<T> retType) throws RpcException {
        if (serviceResponse.hasException()) {
            Throwable t = serviceResponse.getException();
            if(t instanceof RpcException){
                throw (RpcException)t;
            }else
                throw new RpcException(t.getMessage() , t);
        }
        Object value = serviceResponse.getValue();
        return CompatibleTypeUtils.expectConvert(value,retType);
    }
}

