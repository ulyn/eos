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
import com.sunsharing.eos.common.rpc.Server;
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
public class ServiceContext extends AbstractServiceContext {
    Logger logger = Logger.getLogger(ServiceContext.class);

    public ServiceContext(ApplicationContext ctx, String packagePath) {
        super(ctx, packagePath);
    }

    @Override
    protected Object createBean(final Class interfaces, ServiceConfig config) {
        //服务端,找实现类
        if (!config.getImpl().equals("")) {
            //有配置实现类，直接使用
            if (this.ctx != null) {
                Object o = this.ctx.getBean(config.getImpl());
                if (o != null) {
                    this.services.put(interfaces.getName(), o);
                }
            }
            if (!this.services.containsKey(interfaces.getName())) {
                //还没有接口实现类，说明spring没有，那么实例化它
                try {
                    this.services.put(interfaces.getName(), Class.forName(config.getImpl()).newInstance());
                } catch (Exception e) {
                    logger.error("初始化" + interfaces.getName() + "实现类" + config.getImpl() + "异常!", e);
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
                        this.services.put(interfaces.getName(), springBeanMap.get(key));
                        break;
                    }
                }
            }
            if (!this.services.containsKey(interfaces.getName())) {
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
                        this.services.put(interfaces.getName(), clazz.newInstance());
                    } catch (Exception e) {
                        logger.error("实例化EosService实现类" + clazz.getName() + "失败，系统退出", e);
                        System.exit(0);
                    }
                } else {
                    logger.error("找不到EosService接口实现类，系统退出");
                    System.exit(0);
                }
            }
        }
        //创建bean结束，服务端注册
        Server server = ServerFactory.getServer(config.getTransporter());
        server.register(this.services.get(interfaces.getName()), config);

        return this.services.get(interfaces.getName());
    }

    public static void main(String[] args) {
        ServiceContext context = new ServiceContext(null, "com.sunsharing.eos");

    }
}

