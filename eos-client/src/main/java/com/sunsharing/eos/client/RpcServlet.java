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

import com.sunsharing.eos.client.proxy.AbstractProxy;
import com.sunsharing.eos.client.proxy.ProxyFactory;
import com.sunsharing.eos.client.sys.SysParamVar;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.config.ServiceMethod;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
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
    SysParamVar sysParamVar = null;

    public RpcServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("eos framework init RpcServlet....");
        String sysParamVarClass = config.getInitParameter("sysParamVar");
        if (StringUtils.isBlank(sysParamVarClass)) {
            logger.warn("RpcServlet 没有配置SysParamVar的实现类，系统不支持变量入参");
        } else {
            try {
                sysParamVar = (SysParamVar) Class.forName(sysParamVarClass).newInstance();
                sysParamVar.init();
                logger.info("RpcServlet 成功加载配置SysParamVar的实现类，系统支持变量入参");
            } catch (Exception e) {
                logger.error("RpcServlet配置SysParamVar的实现类" + sysParamVarClass + "实例化失败！", e);
                System.exit(0);
            }
        }

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
        //设置上下文
        RpcServletContext.setRequest(req);
        RpcServletContext.setResponse(resp);

        RpcContext rpcContext = RpcContextContainer.getRpcContext();
        if (rpcContext == null) {
            rpcContext = new RpcContext();
            rpcContext.setUserAgent("eos-front");
            rpcContext.setRemoteAddr(req.getRemoteAddr());
            RpcContextContainer.setRpcContext(rpcContext);
        }

        Map rtnMap = new HashMap();
        try {
            String serviceId = req.getParameter("eos_service_id");
            String appId = req.getParameter("eos_appid");
            ServiceConfig serviceConfig = null;
            if (StringUtils.isBlank(appId)) {
                List<ServiceConfig> serviceConfigList = ServiceContext.getInstance().getServiceConfig(serviceId);
                if (serviceConfigList == null) {
                    throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有指定的服务接口：" + serviceId);
                }
                switch (serviceConfigList.size()) {
//                    case 0:
//                        throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有指定的服务接口：" + serviceId);
                    case 1:
                        serviceConfig = serviceConfigList.get(0);
                        break;
                    default:
                        String methodName = req.getParameter("eos_method_name");
                        for (ServiceConfig config : serviceConfigList) {
                            ServiceMethod serviceMethod = config.getMethod(methodName);
                            if (serviceMethod != null) {
                                serviceConfig = config;

                            }
                        }
                        break;
                }
            } else {
                serviceConfig = ServiceContext.getInstance().getServiceConfig(appId, serviceId);
            }
            if (serviceConfig == null) {
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有指定的服务接口：" + appId + "-" + serviceId);
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
//            invocation.setParameterTypes(serviceMethod.getParameterTypes());
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
                    } else {
                        if (parameterValue.startsWith("${") && parameterValue.endsWith("}")) {
                            //判断是否是变量，是变量则取得变量的值
                            parameterValue = sysParamVar.getParamVariable(req, parameterValue.substring(2, parameterValue.length() - 1));
                        }
                    }
                    argsList.add(CompatibleTypeUtils.compatibleTypeConvert(parameterValue, parameterType));
                }
                if (argsList.size() > 0) {
                    invocation.setArguments(argsList.toArray());
                }
            }

            AbstractProxy proxy = ProxyFactory.createProxy(serviceConfig.getProxy());
            Object o = proxy.doInvoke(invocation, serviceConfig);

            rtnMap.put("status", true);
            rtnMap.put("result", o);
        } catch (Throwable throwable) {
            logger.error("remote操作异常！", throwable);
            rtnMap.put("status", false);
            rtnMap.put("result", StringUtils.isBlank(throwable.getMessage()) ? "内部处理异常，未抛出异常说明！" : throwable.getMessage());
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

