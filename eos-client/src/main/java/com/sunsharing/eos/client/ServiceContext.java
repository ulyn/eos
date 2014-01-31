/**
 * @(#)AbstractServiceContext
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午5:37
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client;

import com.sunsharing.eos.client.proxy.AbstractProxy;
import com.sunsharing.eos.client.proxy.ProxyFactory;
import com.sunsharing.eos.common.config.AbstractServiceContext;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.utils.ClassFilter;
import com.sunsharing.eos.common.utils.ClassUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.List;
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
public class ServiceContext extends AbstractServiceContext {
    Logger logger = Logger.getLogger(ServiceContext.class);

    public ServiceContext(ApplicationContext ctx, String packagePath) {
        super(ctx, packagePath);
    }

    @Override
    protected void createBean(final Class interfaces, ServiceConfig config) {
        //客户端,找实现代理类
        AbstractProxy proxy = ProxyFactory.createProxy(config.getProxy());
        services.put(interfaces.getName(), proxy.getProxy(interfaces, config));
    }

    //取得服务bean
    public static <T> T getBean(Class<T> clazz) {
        Object o = services.get(clazz.getName());
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    public static void main(String[] args) {
        ServiceContext context = new ServiceContext(null, "com.sunsharing.eos");

    }
}

