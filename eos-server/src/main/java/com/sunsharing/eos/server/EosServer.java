/**
 * @(#)EosInit
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-10 上午10:18
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */

package com.sunsharing.eos.server;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.component.resvalidate.config.loader.prop.AbstractProp;
import com.sunsharing.eos.server.sys.EosServerProp;
import com.sunsharing.eos.server.zookeeper.ServiceRegister;

import org.springframework.context.ApplicationContext;

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
public class EosServer {
    private static boolean inited = false;

    /**
     * 初始化eos系统，没有使用spring
     *
     * @param packagePath
     */
    public synchronized static void start(String packagePath) {
        start(null, packagePath);
    }

    public synchronized static void start(String packagePath, AbstractProp abstractProp) {
        start(null, packagePath, abstractProp);
    }

    /**
     * 初始化eos系统,使用spring
     *
     * @param ctx
     * @param packagePath
     */
    public synchronized static void start(ApplicationContext ctx, String packagePath) {
        start(null, packagePath, null);
    }

    /**
     * 初始化eos系统,使用spring
     *
     * @param ctx
     * @param packagePath
     */
    public synchronized static void start(
        ApplicationContext ctx,
        String packagePath,
        AbstractProp abstractProp) {
        if (!inited) {
            ConfigContext.instancesBean(EosServerProp.class, abstractProp);

            ServerServiceContext.getInstance().initPackagePath(ctx, packagePath);
            ServerServiceContext.getInstance().initConfig("EosServerContext.xml");
            ServerServiceContext.getInstance().initService();
            new Thread() {
                public void run() {
                    ServiceRegister serviceRegister = ServiceRegister.getInstance();
                    serviceRegister.init();
                }
            }.start();
            inited = true;
        }
    }
}

