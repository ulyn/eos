/**
 * @(#)RpcContext
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-4-1 上午10:39
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>   请求调用时，上下文信息
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class RpcContext implements Serializable {
    //请求者地址
    private String remoteAddr = "";
    //userAgent,表明是java调用的还是前端js调用
    private String userAgent = "java_eos_client";
    //额外参数map
    private Map attributeMap = new HashMap();

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public Object getAttribute(String name) {
        return attributeMap.get(name);
    }

    public void setAttribute(String name, Object value) {
        this.attributeMap.put(name, value);
    }

    public Map getAttributeMap() {
        return attributeMap;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "RpcContext{" +
                "remoteAddr='" + remoteAddr + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", attributeMap=" + attributeMap +
                '}';
    }
}

