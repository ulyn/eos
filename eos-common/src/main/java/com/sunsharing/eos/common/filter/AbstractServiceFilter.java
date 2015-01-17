/**
 * @(#)AbstractServiceFilter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-11 下午3:21
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.filter;

import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;

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
public abstract class AbstractServiceFilter {
    private List<String> pathRegexList = new ArrayList<String>();
    private List<String> pathPatterns = new ArrayList<String>();
    private List<String> excludePaths = new ArrayList<String>();

    public List<String> getPathPatterns() {
        return pathPatterns;
    }

    public void setPathPatterns(List<String> pathPatterns) {
        this.pathPatterns = pathPatterns;
        if (pathPatterns != null && pathPatterns.size() > 0) {
            for (String pattern : pathPatterns) {
                this.pathRegexList.add(pattern.replaceAll("\\*", "(.*)"));
            }
        } else {
            this.pathRegexList.clear();
        }
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public boolean matches(String appId, String serviceId) {
        String path = appId + "." + serviceId;
        if (excludePaths != null && excludePaths.contains(path)) {
            return false;
        } else {
            for (String regex : pathRegexList) {
                if (path.matches(regex)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 执行过滤
     */
    protected abstract void doFilter(ServiceRequest req, ServiceResponse res, FilterChain filterChain) throws ServiceFilterException, RpcException;

}

