/**
 * @(#)ServiceInvoke
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-11 下午7:09
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.server.transporter;

import com.alibaba.fastjson.JSON;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.config.ServiceMethod;
import com.sunsharing.eos.common.filter.*;
import com.sunsharing.eos.common.rpc.*;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
public class ServiceInvokeFilter extends AbstractServiceFilter {
    private Logger logger = Logger.getLogger(ServiceInvokeFilter.class);
    private Map<String, Object> serviceEngine = new HashMap<String, Object>();
    private Map<String, ServiceConfig> serviceConfigEngine = new HashMap<String, ServiceConfig>();

    public ServiceInvokeFilter(Map<String, Object> serviceEngine, Map<String, ServiceConfig> serviceConfigEngine) {
        this.serviceEngine = serviceEngine;
        this.serviceConfigEngine = serviceConfigEngine;
    }

    /**
     * 执行过滤
     *
     * @param req
     * @param res
     * @param fc
     */
    @Override
    protected void doFilter(ServiceRequest req, ServiceResponse res, FilterChain fc) throws ServiceFilterException, RpcException {
        try {
            doServiceInvoke(req, res);
            fc.doFilter(req, res);
        } catch (Exception e) {
            throw new RpcException(e.getMessage(), e);
        }
    }

    private void doServiceInvoke(ServiceRequest req, ServiceResponse res) {
        if (logger.isDebugEnabled()) {
            logger.debug(JSON.toJSONString(req));
        }
        Object obj = this.serviceEngine.get(req.getServiceId());
        ServiceConfig serviceConfig = this.serviceConfigEngine.get(req.getServiceId());
//        RpcResult result = new RpcResult();
        if (obj != null) {
            try {
                ServiceMethod method = serviceConfig.getMethod(req.getMethod());
                if (method == null) {
                    throw new NoSuchMethodException(req.getMethod() + "的ServiceMethod==null");
                }

                //这边暂时直接使用jdk代理执行
                //此处的parameterTypes不用invocation的，规定不允许方法重载
                Method m = obj.getClass().getMethod(req.getMethod(), method.getParameterTypes());
                Object o = m.invoke(obj, null);  //todo

                res.writeValue(o);
            } catch (NoSuchMethodException e) {
                String errorMsg = "has no these class serviceId：" + req.getServiceId() + " - " + req.getMethod();
                logger.error(errorMsg, e);
                res.writeError(new IllegalArgumentException(errorMsg, e));
            } catch (InvocationTargetException e) {
                logger.error("处理服务InvocationTargetException异常", e);
                res.writeError(e.getTargetException());
            } catch (Exception th) {
                String errorMsg = "执行反射方法异常" + serviceConfig.getId() + " - " + req.getMethod();
                logger.error(errorMsg, th);
                ByteArrayOutputStream input = new ByteArrayOutputStream();
                th.printStackTrace(new PrintStream(input));
                res.writeError(new RpcException(RpcException.REFLECT_INVOKE_EXCEPTION, "服务端异常：" + input.toString()));
            }
        } else {
            String errorMsg = "has no these class serviceId：" + req.getServiceId() + " - " + req.getMethod();
            logger.error(errorMsg);
            res.writeError(new IllegalArgumentException(errorMsg));
        }
    }

}

