/**
 * @(#)ProxyFilter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-12 下午3:53
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.eos.common.filter.AbstractServiceFilter;
import com.sunsharing.eos.common.filter.FilterChain;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 代理过滤器
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public abstract class ProxyFilter extends AbstractServiceFilter {
    /**
     * 执行过滤
     *
     * @param req
     * @param res
     * @param filterChain
     */
    @Override
    protected void doFilter(ServiceRequest req, ServiceResponse res, FilterChain filterChain) throws Exception {
        process(req, res);
//        filterChain.doFilter(req,res);代理走，不继续往下执行了
    }

    public abstract void process(ServiceRequest req, ServiceResponse res);
}

