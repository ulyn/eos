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

import com.alibaba.fastjson.JSON;
import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.exception.ExceptionHandler;
import com.sunsharing.eos.common.filter.FilterChain;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;

import java.io.IOException;
import java.lang.reflect.Type;

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
public class DynamicRpc {

    private static RpcCaller caller = new RpcCaller();

    public static RpcCaller getCaller() {
        return caller;
    }

    public static void invoke(ServiceRequest request) throws RpcException {
        invoke(request, void.class);
    }

    /**
     * 执行调用
     *
     * @param retType
     * @param <T>
     * @return
     * @throws com.sunsharing.eos.common.rpc.RpcException
     */
    public static <T> T invoke(ServiceRequest request, Type retType) throws RpcException {
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

    /**
     * 执行调用
     *
     * @param retType
     * @param <T>
     * @return
     * @throws com.sunsharing.eos.common.rpc.RpcException
     */
    @Deprecated
    public static <T> T invoke(ServiceRequest request, Class<T> retType) throws RpcException {
        return invoke(request, (Type) retType);
    }

    public static void invoke(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
        FilterChain filterChain =
            ServiceContext.getInstance().getFilterManager().createFilterChain(serviceRequest.getAppId(), serviceRequest.getServiceId());
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
     * 此方法存在当作为proxy的时候，不存在对象类导致对象序列化类型丢失的风险
     * @param serviceReqBase64Str ServiceRequest对象的base64字符串
     * @return
     */
    @Deprecated
    public static String invoke(String serviceReqBase64Str) throws RpcException {
        ServiceRequest request = null;
        try {
            request = ServiceRequest.formBytes(Base64.decode(serviceReqBase64Str));
        } catch (IOException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, e);
        } catch (ClassNotFoundException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, e);
        }
        ServiceResponse response = new ServiceResponse(request);
        invoke(request, response);
        try {
            return Base64.encode(response.toBytes());
        } catch (IOException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, e);
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
    @Deprecated
    protected static <T> T getResult(ServiceRequest serviceRequest, ServiceResponse serviceResponse, Class<T> retType) throws RpcException {
        checkException(serviceResponse);
        Object value = serviceResponse.getValue();
        return CompatibleTypeUtils.expectConvert(value, retType);
    }

    protected static <T> T getResult(ServiceRequest serviceRequest, ServiceResponse serviceResponse, Type retType) throws RpcException {
        checkException(serviceResponse);
        Object value = serviceResponse.getValue();

        if (retType.getClass() == Class.class) {
            return CompatibleTypeUtils.expectConvert(value, (Class<? extends T>) retType);
        } else {
            if (value instanceof String) {
                return JSON.parseObject((String) value, retType);
            } else {
                return JSON.parseObject(JSON.toJSONString(value), retType);
            }
        }
    }

    private static void checkException(ServiceResponse serviceResponse) {
        if (serviceResponse.hasException()) {
            Throwable t = serviceResponse.getException();
            if (t instanceof RpcException) {
                throw (RpcException) t;
            } else
                throw new RpcException(t.getMessage(), t);
        }
    }

    private static void s(Class cls) {
        System.out.println(cls);
        System.out.println(cls == Class.class);
        System.out.println(cls.isAssignableFrom(Class.class));
    }

    public static void main(String[] args) {
        s(String.class);
    }
}

