/*
 * @(#) eosManage
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2017
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ningyp
 * <br> 2017-12-15 18:39:04
 * <br> @version 1.0
 * ————————————————————————————————
 *    修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 * ————————————————————————————————
 */

package com.sunsharing.idea_plugin.eos;


import com.sunsharing.idea_plugin.configurator.ProjectConfig;
import com.sunsharing.idea_plugin.utils.EventLogger;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class EosManage {

    private static CloseableHttpClient httpClient = null;
    private static HttpClientContext context = null;
    private static CookieStore cookieStore = null;
    private static RequestConfig requestConfig = null;

    static {
        init();
    }

    private static void init() {
        context = HttpClientContext.create();
        cookieStore = new BasicCookieStore();
        // 配置超时时间
        requestConfig = RequestConfig.custom().setConnectTimeout(120000).setSocketTimeout(60000)
            .setConnectionRequestTimeout(60000).build();
        // 设置默认跳转以及存储cookie
        httpClient = HttpClientBuilder.create().setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
            .setRedirectStrategy(new DefaultRedirectStrategy()).setDefaultRequestConfig(requestConfig)
            .setDefaultCookieStore(cookieStore).build();
    }

    /**
     * http get
     *
     * @param url
     * @return response
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse get(String url) throws ClientProtocolException, IOException {
        System.out.println("get url : " + url);
        HttpGet httpget = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(httpget, context);
//        try {
//            cookieStore = context.getCookieStore();
//            List<Cookie> cookies = cookieStore.getCookies();
//            for (Cookie cookie : cookies) {
//                LOG.debug("key:" + cookie.getName() + "  value:" + cookie.getValue());
//            }
//        } finally {
//            response.close();
//        }
        return response;
    }

    /**
     *
     * @param file
     * @param serviceName 服务名称
     * @param module 磨矿
     * @param projectConfig
     * @param <T>
     * @return
     * @throws IOException
     */
    public <T> EosResult<T> postJavaFile(File file, String serviceName, String module, ProjectConfig projectConfig) throws IOException {

        String url = eosUrl + "/uploadjava.do";
        HttpPost httpPost = new HttpPost(url);
        ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
            .addTextBody("servicename", serviceName, contentType)
            .addTextBody("appId", projectConfig.getAppId(), contentType)
            .addTextBody("module", module, contentType)
            .addBinaryBody("java", file);
        httpPost.setEntity(multipartEntityBuilder.build());
        //  httpPost.setHeader("Content-Type", "multipart/form-data;"); //这个有坑
        System.out.println("executing request " + httpPost.getRequestLine());
        CloseableHttpResponse response = httpClient.execute(httpPost, context);
        try {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                EventLogger.log("Response content length: " + resEntity.getContentLength());
            }
            int code = response.getStatusLine().getStatusCode();
            if (200 != code) {
                return new EosResult(false, "http响应 状态码非200啦！！！", null);
            }
            String result = EntityUtils.toString(resEntity);
            if (result != null && result.indexOf("\"status\":true") != -1) {
                return new EosResult(true, "文件上传成功", null);
            } else {
                System.out.println(result);
                return new EosResult(false, "文件上传出问题啦！", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.close();
        }
        return new EosResult(false, "文件上传出问题啦！", null);
    }


    /**
     * http post
     *
     * @param url
     * @param parameters
     *            form表单
     * @return response
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse post(String url, Object... parameters)
        throws IOException {
        List<NameValuePair> nvps = toNameValuePairList(parameters);
        System.out.println("post content :" + nvps);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        System.out.println("executing request " + httpPost.getRequestLine());
        CloseableHttpResponse response = httpClient.execute(httpPost, context);
        try {
            cookieStore = context.getCookieStore();
            List<org.apache.http.cookie.Cookie> cookies = cookieStore.getCookies();
            for (org.apache.http.cookie.Cookie cookie : cookies) {
                System.out.println("key:" + cookie.getName() + "  value:" + cookie.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    @Deprecated
    public void postTestFromData() throws IOException {
        try {
            String uri = "http://192.168.0.235:8100/uploadjava.do";
            HttpPost httpPost = new HttpPost(uri);
            String path = "E:\\IdeaProjects\\sunsharing\\blend-basic\\blend-basic-server\\src\\main\\java\\com\\sunsharing\\blend\\basic\\server\\service\\api\\AddressService.java";
            File file = new File(path);
            // httppost.setEntity(reqEntity);
            ContentType contentType = ContentType.create(HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                .addTextBody("servicename", "测试服务", contentType)
                .addTextBody("appId", "11", contentType)
                .addTextBody("module", "通用", contentType)
                .addBinaryBody("java", file);
            httpPost.setEntity(multipartEntityBuilder.build());
            //  httpPost.setHeader("Content-Type", "multipart/form-data;"); //这个有坑
            System.out.println("executing request " + httpPost.getRequestLine());
            CloseableHttpResponse response = httpClient.execute(httpPost, context);

            try {
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    System.out.println("Response content length: " + resEntity.getContentLength());
                    System.out.println(EntityUtils.toString(response.getEntity()));
                }
                EntityUtils.consume(resEntity);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }

    }

    @SuppressWarnings("unused")
    private static List<NameValuePair> toNameValuePairList(Object... parameters) {
        if (parameters == null) {
            return new ArrayList<NameValuePair>();
        }
        if (parameters.length % 2 != 0) {
            throw new RuntimeException("参数个数不足");
        }
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        for (int i = 0; i < parameters.length; i++) {
            String key = parameters[i].toString();
            i++;
            String value = parameters[i] == null ? "" : parameters[i].toString();
            nvps.add(new BasicNameValuePair(key, value));
        }
        return nvps;
    }


    private String eosUrl;
    private String username;
    private String password;

    public EosManage(String eosUrl, String username, String password) {
        if (!eosUrl.endsWith("/")) {
            eosUrl = eosUrl + "/";
        }
        this.eosUrl = eosUrl;
        this.password = password;
        this.username = username;
    }


    public EosResult<String> login() {
        return eosPost("/login.do", String.class,
            EosResultAdapter.DEFAULT,
            "username", username
            , "pwd", password
        );
    }

    public <T> EosResult<T> eosPost(String api, Class<T> cls,
                                    EosResultAdapter eosResultAdapter,
                                    Object... parameters) {
        String url = eosUrl + api;
        try {
            CloseableHttpResponse httpResponse = EosManage.post(url, parameters);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                String str = EntityUtils.toString(entity);
                return eosResultAdapter.from(str, cls);
            } else {
                return new EosResult(false, "http响应 null entity", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new EosResult(false, "ServerResponseError:" + e.getMessage(), null);
        }
    }

    public <T> EosResult<T> eosGet(String api, Class<T> cls, Object[] parameters) {
        String url = eosUrl + api;
        try {
            if (parameters != null) {
                List<org.apache.http.NameValuePair> nvps = toNameValuePairList(parameters);
                url = url + "&" + URLEncodedUtils.format(nvps, "UTF-8");
            }
            CloseableHttpResponse httpResponse = EosManage.get(url);
            HttpEntity entity = httpResponse.getEntity();
            if (entity != null) {
                String str = EntityUtils.toString(entity);
                return EosResultAdapter.DEFAULT.from(str, cls);
            } else
                return new EosResult(false, "http响应 null entity", null);

        } catch (Exception e) {
            e.printStackTrace();
            return new EosResult(false, "ServerResponseError:" + e.getMessage(), null);
        }
    }


    public static void main(String[] args) {
        EosManage eosManage = new EosManage("http://192.168.0.235:8100", "宁永鹏", "123456");
        EosResult<String> result = eosManage.login();
        System.out.println(result);

    }

}

