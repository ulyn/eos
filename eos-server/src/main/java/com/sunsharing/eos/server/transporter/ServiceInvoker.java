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
import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.config.ServiceMethod;
import com.sunsharing.eos.common.filter.AbstractServiceFilter;
import com.sunsharing.eos.common.filter.FilterChain;
import com.sunsharing.eos.common.filter.ServiceFilterException;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.server.sys.EosServerProp;
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
public class ServiceInvoker extends AbstractServiceFilter {
    private Logger logger = Logger.getLogger(ServiceInvoker.class);
    private Map<String, Object> serviceEngine = new HashMap<String, Object>();
    private Map<String, ServiceConfig> serviceConfigEngine = new HashMap<String, ServiceConfig>();

    public ServiceInvoker(Map<String, Object> serviceEngine, Map<String, ServiceConfig> serviceConfigEngine) {
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
        if (obj != null) {
            try {
                ServiceMethod serviceMethod = serviceConfig.getMethod(req.getMethod());
                if (serviceMethod == null) {
                    throw new NoSuchMethodException(
                            String.format("服务%s没有指定的方法：%s",req.getServiceId(),req.getMethod()));
                }

                //这边暂时直接使用jdk代理执行
                Method m = serviceMethod.getMethod();
                String[] parameterNames = serviceMethod.getParameterNames();
                Object[] args = null;
                if(parameterNames != null){
                    Class<?>[] parameterTypes = m.getParameterTypes();
                    args = new Object[parameterTypes.length];
                    for(int i=0,l= args.length;i<l;i++){
                        String parameterName = parameterNames[i];
                        args[i] = req.getParameter(parameterName,parameterTypes[i]);
                    }
                }
                Object o = m.invoke(obj, args);
                res.writeValue(o);
            } catch (NoSuchMethodException e) {
                String errorMsg = "反射执行未找到指定方法：" + req.getServiceId() + " - " + req.getMethod();
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
            String errorMsg = String.format("%s(%s) 没有发现指定的serviceId：%s"
                    ,EosServerProp.appId,EosServerProp.localIp,req.getServiceId());
            logger.error(errorMsg);
            res.writeError(new IllegalArgumentException(errorMsg));
        }
    }

}

