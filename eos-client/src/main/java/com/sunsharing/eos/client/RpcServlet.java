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
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunsharing.eos.client.mock.MockUtils;
import com.sunsharing.eos.client.rpc.DynamicRpc;
import com.sunsharing.eos.client.sys.EosClientProp;
import com.sunsharing.eos.common.ServiceRequest;
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
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
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

    private String accessControlAllowOrigin;

    public RpcServlet() {
        super();
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        logger.info("eos framework init RpcServlet....");
        serialization = config.getInitParameter("serialization");
        transporter = config.getInitParameter("transporter");
        String timeoutStr = config.getInitParameter("timeout");
        if(!StringUtils.isBlank(timeoutStr)){
            timeout = Integer.valueOf(timeoutStr);
        }

        accessControlAllowOrigin = config.getInitParameter("Access-Control-Allow-Origin");

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
            builder.setDebugServerIp(EosClientProp.getDebugServerIp(appId));

            //设置请求参数
            Map<String,String[]> params = req.getParameterMap();
            for(String p : params.keySet()){
                String[] vs = params.get(p);
                builder.setParameter(p,vs == null? null : vs[0]);
            }

            //是否模拟的参数
            String mock = req.getParameter("eos_mock");
            Object result;
            if(!StringUtils.isBlank(mock) && EosClientProp.use_mock){
                MockUtils mockUtils = new MockUtils();
                result = mockUtils.transMockMatch(appId,serviceId,version,methodName,mock,builder.build().getParameterMap());
            }else{
                result = DynamicRpc.invoke(builder.build(),Object.class);
            }
            rtnMap.put("result", CompatibleTypeUtils.tryConvertStrToObject(result));
            rtnMap.put("status", true);
        } catch (Throwable throwable) {
            logger.error("remote操作异常！", throwable);
            rtnMap.put("status", false);
            rtnMap.put("result", StringUtils.isBlank(throwable.getMessage()) ? "内部处理异常，未抛出异常说明！" : throwable.getMessage());
        }finally {
            RpcContextContainer.remove();
            WebContext.remove();
        }

        String content =  JSON.toJSONString(rtnMap
                , SerializerFeature.WriteMapNullValue
                , SerializerFeature.WriteClassName);

        OutputStream outputStream = getOutputStream(resp);
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", -1);
        resp.setContentType("application/json;charset=UTF-8");
        if(!StringUtils.isBlank(accessControlAllowOrigin)){
            resp.setHeader("Access-Control-Allow-Origin",accessControlAllowOrigin);
        }
        outputStream.write(content.getBytes("UTF-8"));
    }


    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * 取得response对象的输出流，确保PrintWriter或者ServletOutputStream只能调用一次的问题
     * @param response
     * @return
     */
    public OutputStream getOutputStream(HttpServletResponse response){
        try {
            return response.getOutputStream();
        } catch (Exception e) {
            try {
                return new WriterOutputStream(response.getWriter());
            } catch (IOException e1) {
                logger.error(e1.getMessage(), e1);
            }
        }
        return null;
    }


    /* ------------------------------------------------------------ */
    /** Wrap a Writer as an OutputStream.
     * When all you have is a Writer and only an OutputStream will do.
     * Try not to use this as it indicates that your design is a dogs
     * breakfast (JSP made me write it).
     *
     */
    public static class WriterOutputStream extends OutputStream
    {
        protected final Writer _writer;
        protected final Charset _encoding;
        private final byte[] _buf=new byte[1];

        /* ------------------------------------------------------------ */
        public WriterOutputStream(Writer writer, String encoding)
        {
            _writer=writer;
            _encoding=encoding==null?null:Charset.forName(encoding);
        }

        /* ------------------------------------------------------------ */
        public WriterOutputStream(Writer writer)
        {
            _writer=writer;
            _encoding=null;
        }

        /* ------------------------------------------------------------ */
        @Override
        public void close()
                throws IOException
        {
            _writer.close();
        }

        /* ------------------------------------------------------------ */
        @Override
        public void flush()
                throws IOException
        {
            _writer.flush();
        }

        /* ------------------------------------------------------------ */
        @Override
        public void write(byte[] b)
                throws IOException
        {
            if (_encoding==null)
                _writer.write(new String(b));
            else
                _writer.write(new String(b,_encoding));
        }

        /* ------------------------------------------------------------ */
        @Override
        public void write(byte[] b, int off, int len)
                throws IOException
        {
            if (_encoding==null)
                _writer.write(new String(b,off,len));
            else
                _writer.write(new String(b,off,len,_encoding));
        }

        /* ------------------------------------------------------------ */
        @Override
        public synchronized void write(int b)
                throws IOException
        {
            _buf[0]=(byte)b;
            write(_buf);
        }
    }
}

