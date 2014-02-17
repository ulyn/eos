/**
 * @(#)RpcServlet
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-6 下午10:29
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.sunsharing.eos.client.proxy.AbstractProxy;
import com.sunsharing.eos.client.proxy.ProxyFactory;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.config.ServiceMethod;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

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
public class RpcServlet extends HttpServlet {
    Logger logger = Logger.getLogger(RpcServlet.class);

    public RpcServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("eos framework init RpcServlet....");
        String scanPackage = config.getInitParameter("scanPackage");
        if (StringUtils.isBlank(scanPackage)) {
            scanPackage = "com.sunsharing";
        }
        EosInit.start(scanPackage);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map rtnMap = new HashMap();
        try {
            String serviceId = req.getParameter("eos_service_id");
            ServiceConfig serviceConfig = ServiceContext.getServiceConfig(serviceId);
            if (serviceConfig == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有指定的服务接口：" + serviceId);
            }

            String methodName = req.getParameter("eos_method_name");
            ServiceMethod serviceMethod = serviceConfig.getMethod(methodName);
            if (serviceMethod == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "指定的服务接口：" + serviceId + "没有方法名：" + methodName);
            }

            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(methodName);
//            invocation.setId(serviceId);
//            invocation.setRetType(serviceMethod.getRetType().getName());
            invocation.setParameterTypes(serviceMethod.getParameterTypes());
            //是否模拟的参数
            String mock = req.getParameter("eos_mock");
            invocation.setMock(mock);

            //方法入参
            if (serviceMethod.getParameterTypes() != null) {

                Class<?>[] parameterTypes = serviceMethod.getParameterTypes();
                String[] parameterNames = serviceMethod.getParameterNames();

                List argsList = new ArrayList();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class parameterType = parameterTypes[i];
                    String parameterName = parameterNames[i];
                    String parameterValue = req.getParameter(parameterName);
                    if (parameterValue == null) {
                        logger.warn("指定的服务接口：" + serviceId + "的方法：" + methodName + "参数" + parameterName + "值为null");
                    }
                    argsList.add(CompatibleTypeUtils.compatibleTypeConvert(parameterValue, parameterType));
                }
                if (argsList.size() > 0) {
                    invocation.setArguments(argsList.toArray());
                }
            }


            AbstractProxy proxy = ProxyFactory.createProxy(serviceConfig.getProxy());
            Object o = proxy.getRpcResult(invocation, serviceConfig, serviceMethod.getRetType());

            rtnMap.put("status", true);
            rtnMap.put("result", o);
        } catch (Throwable throwable) {
            logger.error("remote操作异常！", throwable);
            rtnMap.put("status", false);
            rtnMap.put("result", throwable.getMessage());
        }

        String jsonp = req.getParameter("eos_jsonp_callback");
        String content = CompatibleTypeUtils.objectToString(rtnMap);
        if (!StringUtils.isBlank(jsonp)) {
            content = jsonp + "(" + content + ")";
        }
        printOutContent(resp, content);
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    /**
     * 输出响应流
     *
     * @param response
     * @param content：要输出的内容
     * @return
     */
    public void printOutContent(HttpServletResponse response, String content) {
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = null;
        try {
            writer = response.getWriter();
            // response.setContentLength(responseContent.length());
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("消息输出出错", e);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

