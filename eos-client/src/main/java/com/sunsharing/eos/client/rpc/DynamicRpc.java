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

import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.common.exception.ExceptionHandler;
import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;

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
            Object o = getResult(request, serviceResponse, retType);
            return (T) o;
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
    protected static Object getResult(ServiceRequest serviceRequest, ServiceResponse serviceResponse, Class retType) throws RpcException {
        if (serviceResponse.hasException()) {
            throw new RpcException(serviceResponse.getException().getMessage(), serviceResponse.getException());
        }
        boolean isMock = false;//!StringUtils.isBlank(serviceRequest.getMock());

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
}

