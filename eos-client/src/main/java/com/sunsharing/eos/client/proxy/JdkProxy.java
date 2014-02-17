/**
 * @(#)JdkProxy
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-1 上午12:11
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.proxy;

import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
public class JdkProxy extends AbstractProxy {

    @Override
    public <T> T getProxy(Class<T> clazz, final ServiceConfig serviceConfig) throws RpcException {
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcInvocation invo = new RpcInvocation();
//                invo.setId(serviceConfig.getId());
                invo.setMethodName(method.getName());
                invo.setParameterTypes(method.getParameterTypes());
                invo.setArguments(args);
//                invo.setRetType(CompatibleTypeUtils.getSimpleName(method.getReturnType()));

                return getRpcResult(invo, serviceConfig, method.getReturnType());
            }
        };
        T t = (T) Proxy.newProxyInstance(JdkProxy.class.getClassLoader(), new Class[]{clazz}, handler);
        return t;
    }
}

