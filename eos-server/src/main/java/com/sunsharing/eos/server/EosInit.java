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
import com.sunsharing.eos.server.sys.SysProp;
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
public class EosInit {

    /**
     * 初始化eos系统，没有使用spring
     *
     * @param packagePath
     */
    public static void start(String packagePath) {
        start(null, packagePath);
    }

    /**
     * 初始化eos系统,使用spring
     *
     * @param ctx
     * @param packagePath
     */
    public static void start(ApplicationContext ctx, String packagePath) {
        ConfigContext.instancesBean(SysProp.class);
        ServiceContext serviceContext = new ServiceContext(ctx, packagePath);
        new Thread() {
            public void run() {
                ServiceRegister serviceRegister = ServiceRegister.getInstance();
                serviceRegister.init();
            }
        }.start();
    }
}

