/**
 * @(#)FilterManager
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-11 下午4:17
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.filter;

import com.sunsharing.eos.common.config.AbstractServiceContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 过滤器管理器
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class FilterManager {
    private static Logger logger = Logger.getLogger(FilterManager.class);
    private static List<AbstractServiceFilter> filters = new ArrayList<AbstractServiceFilter>();

    public static FilterChain createFilterChain(String appId, String serviceId) {
        List<AbstractServiceFilter> filters = FilterManager.matchFilters(appId, serviceId);
        FilterChain filterChain = new FilterChain(filters);
        return filterChain;
    }

    public static void registerFilter(String pathRegex, String filterClassName) {
        try {
            AbstractServiceFilter filter = (AbstractServiceFilter) Class.forName(filterClassName).newInstance();
            filter.setPathRegex(pathRegex);
            registerFilter(filter);
        } catch (Exception e) {
            logger.error(String.format("注册过滤器newInstance异常!：%s", filterClassName), e);
        }
    }

    public static void registerFilter(AbstractServiceFilter filter) {
        filters.add(filter);
        logger.info(String.format("注册过滤器：%s", filter.getClass().getName()));
    }

    /**
     * 取得服务的过滤器
     *
     * @param appId
     * @param serviceId
     * @return
     */
    private static List<AbstractServiceFilter> matchFilters(String appId, String serviceId) {
        String path = "/" + appId + "/" + serviceId;
        List<AbstractServiceFilter> filterList = new ArrayList<AbstractServiceFilter>();
        for (int i = 0, l = filters.size(); i < l; i++) {
            AbstractServiceFilter filter = filters.get(i);
            if (path.matches(filter.getPathRegex())) {
                filterList.add(filter);
            }
        }
        return filterList;
    }


}

