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

import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
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
public class RpcServlet extends HttpServlet {
    Logger logger = Logger.getLogger(RpcServlet.class);

    private String serialization;
    private String transporter;
    private int timeout;

    public RpcServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("eos framework init RpcServlet....");
        EosClient.start();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置上下文
        WebContext.setContext(req, resp);
        RpcContextContainer.setRpcContext(req.getHeader("User-Agent"),req.getRemoteAddr());

        Map rtnMap = new HashMap();
        try {
            String serviceId = req.getParameter("eos_service_id");
            String appId = req.getParameter("eos_appid");
            String methodName = req.getParameter("eos_method_name");
            String version = req.getParameter("eos_version");

            //设置上下文
            ServiceRequest.Builder builder = new ServiceRequest.Builder(appId,serviceId,methodName,version);

            builder.setSerialization(serialization);
            builder.setTransporter(transporter);
            builder.setTimeout(timeout);
            builder.setDebugServerIp(SysProp.getDebugServerIp(appId));

            RpcContext context = RpcContextContainer.getRpcContext();
            builder.setRemoteAddr(context.getRemoteAddr());
            builder.setUserAgent(context.getUserAgent());
            builder.setAttributeMap(context.getAttributeMap());
            //设置请求参数
            Map<String,String[]> params = req.getParameterMap();
            for(String p : params.keySet()){
                String[] vs = params.get(p);
                builder.setParameter(p,vs == null? null : vs[0]);
            }

            //是否模拟的参数
            String mock = req.getParameter("eos_mock");
            if(!StringUtils.isBlank(mock) && SysProp.use_mock){
                rtnMap.put("result", "");//使用mock
            }else{
                Object result = DynamicRpc.invoke(builder.build(),Object.class);
                rtnMap.put("result", result);
            }
            rtnMap.put("status", true);
        } catch (Throwable throwable) {
            logger.error("remote操作异常！", throwable);
            rtnMap.put("status", false);
            rtnMap.put("result", StringUtils.isBlank(throwable.getMessage()) ? "内部处理异常，未抛出异常说明！" : throwable.getMessage());
        }finally {
            RpcContextContainer.remove();
            WebContext.remove();
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

