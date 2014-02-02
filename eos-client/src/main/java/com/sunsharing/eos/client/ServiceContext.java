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

    //存储服务对象,key为服务name
    protected static Map<String, Object> interfaceServiceMap = new HashMap<String, Object>();

    public ServiceContext(ApplicationContext ctx, String packagePath) {
        super(ctx, packagePath);
    }

    @Override
    protected Object createBean(final Class interfaces, ServiceConfig config) {
        //客户端,找实现代理类
        AbstractProxy proxy = ProxyFactory.createProxy(config.getProxy());
        Object bean = proxy.getProxy(interfaces, config);
        interfaceServiceMap.put(interfaces.getName(), bean);
        return bean;
    }

    /**
     * 根据接口取得服务bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        Object o = interfaceServiceMap.get(clazz.getName());
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    public static void main(String[] args) {
        ServiceContext context = new ServiceContext(null, "com.sunsharing.eos");

    }
}

