/**
 * @(#)ServiceConfig
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午5:31
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.config;

import com.sunsharing.eos.common.Constants;

import java.util.List;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>  服务配置类
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class ServiceConfig {
    private String id = "";
    private String version;
    private String proxy = Constants.DEFAULT_PROXY;
    private String serialization = Constants.DEFAULT_SERIALIZATION;
    private String transporter = Constants.DEFAULT_TRANSPORTER;
    private String appId = "";
    private int timeout = 30000;
    private String mock = "";

    private String impl = "";

    private Map<String, String> methodMockMap;
    private List<ServiceMethod> serviceMethodList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public boolean isMock() {
        return null == mock || "".equals(mock);
    }

    public String getMock() {
        return mock;
    }

    public void setMock(String mock) {
        this.mock = mock;
    }

    public String getImpl() {
        return impl;
    }

    public void setImpl(String impl) {
        this.impl = impl;
    }

    public Map<String, String> getMethodMockMap() {
        return methodMockMap;
    }

    public void setMethodMockMap(Map<String, String> methodMockMap) {
        this.methodMockMap = methodMockMap;
    }

    public List<ServiceMethod> getServiceMethodList() {
        return serviceMethodList;
    }

    public void setServiceMethodList(List<ServiceMethod> serviceMethodList) {
        this.serviceMethodList = serviceMethodList;
    }

    public ServiceMethod getMethod(String methodName) {
        if (serviceMethodList != null) {
            for (ServiceMethod serviceMethod : serviceMethodList) {
                if (serviceMethod.getMethodName().equals(methodName)) {
                    return serviceMethod;
                }
            }
        }
        return null;
    }
}

