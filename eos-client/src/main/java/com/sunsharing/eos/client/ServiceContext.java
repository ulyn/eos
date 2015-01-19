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
import com.sunsharing.eos.common.exception.ExceptionResolver;
import org.apache.log4j.Logger;

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

    //全局异常处理器
    private static ExceptionResolver exceptionResolver = null;

    public static ExceptionResolver getExceptionResolver() {
        return exceptionResolver;
    }
    //存储服务对象,key为服务name
//    protected static Map<String, Object> interfaceServiceMap = new HashMap<String, Object>();

    public ServiceContext(String packagePath) {
        super(packagePath);
    }

    @Override
    protected Object createBean(final Class interfaces, ServiceConfig config) {
        //客户端,找实现代理类
        AbstractProxy proxy = ProxyFactory.createProxy(config.getProxy());
        Object bean = proxy.getProxy(interfaces, config);
//        interfaceServiceMap.put(interfaces.getName(), bean);
        return bean;
    }

    @Override
    public void setExceptionResolver(ExceptionResolver exceptionResolver) {
        ServiceContext.exceptionResolver = exceptionResolver;
    }

    /**
     * 根据接口取得服务bean
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        Object o = servicesMapByKeyClassName.get(clazz.getName());
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    public static <T> T getBean(String appId, String serviceId) {
        Object o = servicesMapByKeyAppServiceId.get(getServiceConfigKey(appId, serviceId));
        if (o == null) {
            return null;
        }
        return (T) o;
    }

    public static void main(String[] args) {
        ServiceContext context = new ServiceContext("com.sunsharing.eos");

    }
}

