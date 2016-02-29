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
package com.sunsharing.eos.server;

import com.sunsharing.eos.common.config.AbstractServiceContext;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.exception.ExceptionResolver;
import com.sunsharing.eos.common.rpc.RpcServer;
import com.sunsharing.eos.common.utils.ClassFilter;
import com.sunsharing.eos.common.utils.ClassUtils;
import com.sunsharing.eos.server.transporter.ServerFactory;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import java.util.*;

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
public class ServerServiceContext extends AbstractServiceContext {

    private static ServerServiceContext sc = new ServerServiceContext();

    static Logger logger = Logger.getLogger(ServerServiceContext.class);

    ApplicationContext ctx;
    //全局异常处理器
    private static ExceptionResolver exceptionResolver = null;

    public static ExceptionResolver getExceptionResolver() {
        return exceptionResolver;
    }

    private ServerServiceContext()
    {

    }

    public static ServerServiceContext getInstance()
    {
        return sc;
    }

    /**
     * 没有结合spring的构造
     *
     * @param packagePath
     */
    public void initPackagePath(String packagePath) {
        this.packagePath = packagePath;
    }

    /**
     * 结合spring的构造
     *
     * @param ctx
     * @param packagePath
     */
    public void initPackagePath(ApplicationContext ctx, String packagePath) {
        this.packagePath = packagePath;
        this.ctx = ctx;
    }

    @Override
    protected Object createBean(final Class interfaces, ServiceConfig config) {
        //服务端,找实现类
        Object bean = null;
        if (!config.getImpl().equals("")) {
            //有配置实现类，直接使用
            if (this.ctx != null) {
                Object o = this.ctx.getBean(config.getImpl());
                if (o != null) {
                    logger.info(interfaces.getName() + "取得服务配置的impl=" + config.getImpl() + "的spring实现");
                    bean = o;
                }
            }
            if (!this.servicesMapByKeyClassName.containsKey(interfaces.getName())) {
                //还没有接口实现类，说明spring没有，那么实例化它
                try {
                    bean = Class.forName(config.getImpl()).newInstance();
                    logger.info(interfaces.getName() + "取得服务配置的impl=" + config.getImpl() + "的newInstance实现");
                } catch (Exception e) {
                    logger.error("初始化" + interfaces.getName() + "配置的impl实现类" + config.getImpl() + "异常!", e);
                    System.exit(0);
                }
            }
        } else {
            //没有配置，去扫描取得一个实现类
            if (this.ctx != null) {
                //有spring ctx ，先找找是不是Spring 实现
                Map<String, Object> springBeanMap = this.ctx.getBeansOfType(interfaces);
                if (springBeanMap.size() > 0) {
                    //是spring的实现
                    for (String key : springBeanMap.keySet()) {
                        bean = springBeanMap.get(key);
                        logger.info(interfaces.getName() + "取得服务类型的spring实现" + bean.getClass());
                        break;
                    }
                }
            }
            if (bean == null) {
                //没有spring的实现，那么扫描取实现类
                //查找实现类
                ClassFilter filter = new ClassFilter() {
                    @Override
                    public boolean accept(Class clazz) {
                        return interfaces.isAssignableFrom(clazz) && !interfaces.equals(clazz);
                    }
                };
                List<Class> implClasses = ClassUtils.scanPackage(this.packagePath, filter);
                if (implClasses.size() > 0) {
                    Class clazz = implClasses.get(0);
                    try {
                        bean = clazz.newInstance();
                        logger.info(interfaces.getName() + "取得服务类型的newInstance实现" + clazz.getName());
                    } catch (Exception e) {
                        logger.error("实例化EosService实现类" + clazz.getName() + "失败，系统退出", e);
                        System.exit(0);
                    }
                } else {
                    logger.warn("找不到EosService接口" + interfaces.getName() + "实现类");
                    bean = null;
                }
            }
        }
        if (bean != null) {
            //创建bean结束，服务端注册
            RpcServer server = ServerFactory.getServer(config.getTransporter());
            server.register(bean, config);
//            this.services.put(interfaces.getName(), bean);
        }
        return bean;
    }

    @Override
    public void setExceptionResolver(ExceptionResolver exceptionResolver) {
        ServerServiceContext.exceptionResolver = exceptionResolver;
    }

}

