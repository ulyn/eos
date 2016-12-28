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
package com.sunsharing.eos.client;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.component.resvalidate.config.loader.prop.AbstractProp;
import com.sunsharing.eos.client.sys.EosClientProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.zookeeper.ZookeeperUtils;

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
public class EosClient {

    private static boolean inited = false;

    /**
     * 初始化eos系统
     *
     */
    public synchronized static void start(AbstractProp abstractProp) {
        if (!inited) {
            ConfigContext.instancesBean(EosClientProp.class,abstractProp);

            ServiceContext.getInstance().initConfig("EosClientContext.xml");

            new Thread() {
                public void run() {
                    ServiceLocation.getInstance().connect();
                    ZookeeperUtils utils = ZookeeperUtils.getInstance();
                }
            }.start();
            inited = true;
        }
    }
    /**
     * 初始化eos系统
     *
     */
    public synchronized static void start() {
        start(null);
    }

    /**
     * 同步初始化eos系统
     *
     */
    public synchronized static void synStart(){
        synStart(null);
    }
    /**
     * 同步初始化eos系统
     * @param abstractProp 初始配置读取
     */
    public synchronized static void synStart(AbstractProp abstractProp) {
        ConfigContext.instancesBean(EosClientProp.class,abstractProp);

        ServiceContext.getInstance().initConfig("EosClientContext.xml");
        ServiceLocation.getInstance().synConnect();
    }

}

