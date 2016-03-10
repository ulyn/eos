/**
 * @(#)HttpProxyFilter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2015
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 15-1-19 下午7:02
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.clientexample.test;

import com.sunsharing.component.utils.crypto.Base64;
import com.sunsharing.eos.client.rpc.ProxyFilter;
import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.filter.ServiceFilterException;
import com.sunsharing.eos.common.rpc.RpcException;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
public class HttpProxyFilter extends ProxyFilter {
    Logger logger = Logger.getLogger(HttpProxyFilter.class);

    @Override
    public void process(ServiceRequest req, ServiceResponse res) throws ServiceFilterException, RpcException {
        try {
            String base = Base64.encode(req.toBytes());
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("serialization", req.getSerialization());
            paramMap.put("serviceReqBase64Str", base);
            String resutlstr = doHttp("http://192.168.0.60:8095/service.do", paramMap2Str(paramMap), "post", "utf-8");
            ServiceResponse response = ServiceResponse.formBytes(Base64.decode(resutlstr));
            if (response.hasException()) {
                throw new ServiceFilterException(response.getException().getMessage(), response.getException());
            }
            res.writeValue(response.getValue());
        } catch (Exception e) {
            throw new ServiceFilterException(e.getMessage(), e);
        }
    }

    /**
     * 执行http请求
     *
     * @param urlStr
     * @param requestStr
     * @param method
     * @param encoding
     * @return
     * @throws Exception
     */
    public String doHttp(String urlStr, String requestStr, String method, String encoding) throws Exception {
        StringBuffer result = new StringBuffer();
        try {
            if ("get".equalsIgnoreCase(method) && requestStr != null && !requestStr.equals("")) {
                if (urlStr.indexOf("?") != -1) {
                    urlStr = urlStr + "&" + requestStr;
                } else {
                    urlStr = urlStr + "?" + requestStr;
                }
            }
            URL url = new URL(urlStr);
            HttpURLConnection httpConn = (HttpURLConnection) url
                    .openConnection();

            // 设置 http发送相关属性
            httpConn.setUseCaches(false);
            httpConn.setConnectTimeout(2000);
            httpConn.setReadTimeout(15 * 1000);
            httpConn.setDoInput(true);
            if ("post".equalsIgnoreCase(method)) {
                if (requestStr == null) {
                    requestStr = "";
                }
                byte[] requestStrByte = requestStr.getBytes(encoding);
                httpConn.setRequestProperty("Content-Length", String.valueOf(requestStrByte.length));
                httpConn.setRequestProperty("Content-Type", " application/x-www-form-urlencoded");
                httpConn.setRequestMethod(method.toUpperCase());
                httpConn.setDoOutput(true);
                // 写消息
                OutputStream out = httpConn.getOutputStream();
                out.write(requestStrByte);
                out.flush();
                out.close();
            }
            int responseCode = httpConn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) //对应HTTP响应中状态行的响应码
            {
                // 读取结果
                InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(), encoding);
                //System.out.println("对返回串的编码格式："+isr.getEncoding());
                BufferedReader in = new BufferedReader(isr);
                String inputLine = in.readLine();
                while (null != inputLine) {
                    result.append(inputLine);
                    result.append("\n");
                    inputLine = in.readLine();
                }
            } else {
                throw new RuntimeException("http请求[" + url + "]异常，code:" + responseCode);
            }
            if (httpConn != null) {
                httpConn.disconnect();
            }
            return result.toString();
        } catch (Exception e) {
            logger.error("Http请求异常！！！", e);
            throw e;
        }
    }

    /**
     * 将请求的参数map转换为字符串拼接格式
     * <br/>如：{"a":1,"b":2} => a=1&b=2
     *
     * @param requestParamMap
     * @return
     */
    public String paramMap2Str(Map<String, String> requestParamMap) {
        StringBuilder sb = new StringBuilder();
        if (requestParamMap != null) {
            for (String key : requestParamMap.keySet()) {
                sb.append(key).append("=").append(requestParamMap.get(key));
                sb.append("&");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
        }
        return sb.toString();
    }
}

