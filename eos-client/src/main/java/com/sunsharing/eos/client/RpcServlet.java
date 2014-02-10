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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class RpcServlet extends HttpServlet {
    Logger logger = Logger.getLogger(RpcServlet.class);

    public RpcServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String serviceId = req.getParameter("service_id");
            ServiceConfig serviceConfig = ServiceContext.getServiceConfig(serviceId);
            if (serviceConfig == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有指定的服务接口：" + serviceId);
            }

            String methodName = req.getParameter("method_name");
            ServiceMethod serviceMethod = serviceConfig.getMethod(methodName);
            if (serviceMethod == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "指定的服务接口：" + serviceId + "没有方法名：" + methodName);
            }

            RpcInvocation invocation = new RpcInvocation();
            invocation.setMethodName(methodName);
            invocation.setId(serviceId);
            invocation.setRetType(serviceMethod.getRetType().getName());
            invocation.setParameterTypes(serviceMethod.getParameterTypes());

            //方法入参
            if (serviceMethod.getParameterTypes() != null) {
                String arguments = req.getParameter("arguments");
                JSONArray argsArr = JSONArray.parseArray(arguments);
                Class<?>[] parameterTypes = serviceMethod.getParameterTypes();
                if (argsArr.size() != parameterTypes.length) {
                    throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION,
                            "指定的服务接口：" + serviceId + "的方法：" + methodName + "入参个数不匹配，方法入参：" +
                                    Arrays.toString(parameterTypes) + "实际入参：" + arguments);
                }
                List argsList = new ArrayList();
                for (int i = 0; i < parameterTypes.length; i++) {
                    Class parameterType = parameterTypes[i];
                    argsList.add(CompatibleTypeUtils.compatibleTypeConvert(argsArr.getString(i), parameterType));
                }
                invocation.setArguments(argsList.toArray());
            }

            //是否模拟的参数
            String mock = req.getParameter("mock");
            if (!StringUtils.isBlank(mock)) {
                serviceConfig.setMock(mock);
            }

            AbstractProxy proxy = ProxyFactory.createProxy(serviceConfig.getProxy());
            Object o = proxy.getRpcResult(invocation, serviceConfig);

            printOutContent(resp, CompatibleTypeUtils.objectToString(o));
        } catch (Throwable throwable) {
            throw new RpcException(throwable);
        }
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

