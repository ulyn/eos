/**
 * @(#)FilterChain
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-11 下午3:24
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.filter;

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
public class FilterChain {

    private List<AbstractServiceFilter> filters = new ArrayList<AbstractServiceFilter>();
    private int index = 0;//是否结束的标志

    public FilterChain() {
    }

    public FilterChain(List<AbstractServiceFilter> filters) {
        this.filters = filters;
    }

    /**
     * 增加过滤器
     *
     * @param filter
     * @return
     */
    public FilterChain addFilter(AbstractServiceFilter filter) {
        this.filters.add(filter);
        return this;
    }

    /**
     * 执行过滤
     *
     * @param req
     * @param res
     */
    public void doFilter(RequestPro req, ResponsePro res) throws Exception {
        if (index < this.filters.size()) {
            AbstractServiceFilter filter = this.filters.get(index);
            index++;
            filter.doFilter(req, res, this);
        }
    }
}

