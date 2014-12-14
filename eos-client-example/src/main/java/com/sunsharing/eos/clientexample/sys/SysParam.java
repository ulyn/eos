package com.sunsharing.eos.clientexample.sys;

import com.sunsharing.component.resvalidate.config.ConfigContext;
import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import org.apache.log4j.Logger;

import java.util.*;


/**
 * <pre><b><font color="blue">SysParam</font></b></pre>
 * <p/>
 * <pre><b>&nbsp;--描述说明--</b></pre>
 * JDK版本：JDK1.5.0
 *
 * @author <b>李自立</b>
 */
public class SysParam {

    /**
     * 记录日志
     */
    private static Logger logger = Logger.getLogger(SysInit.class);

    /**
     * 应用系统路径
     */
    private static String sysPath = "";

    /**
     * 全局参数缓存Map信息
     */
    private static Map<String, String> configMap;

    /**
     * 构造函数
     */
    public SysParam() {
    }

    public static void setSysPath(String sysPath) {
        SysParam.sysPath = sysPath;
    }

    public static String getSysPath() {
        return sysPath;
    }

    /**
     * 初始化配置文件和数据库的配置信息
     */
    public static void init() {
        configMap = new HashMap<String, String>();
        try {

            //读取数据库中的全局参数的配置，存到缓存中
            initSysParam();

            //读取配置文件中的全局参数的配置，存到缓存中
            initProperties();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新加载配置文件和数据库的配置信息
     */
    public static void reload() {
        configMap = null;
        init();
    }

    /**
     * 读取数据库中的全局参数的配置，存到缓存中
     */
    private static void initSysParam() {
    }

    /**
     * 读取配置文件中的全局参数的配置，存到缓存中
     */
    private static void initProperties() {

    }

    /**
     * 获得属性文件的位置
     */

    //初始化缓存中全局参数map的值
    public static void setConfigMap(Map<String, String> map) {
        if (configMap == null) {
            configMap = new HashMap<String, String>();
        }
        configMap.putAll(map);
    }

    //从缓存属性配置文件中取值
    public static String getConfigMapValue(String properKey) {
        String value = null;
        if (configMap != null) {
            if (configMap.containsKey(properKey)) {
                value = configMap.get(properKey);
            } else {
                logger.error("别名为 " + properKey + " 的配置信息不存在或未加载,请刷新全局配置参数！");
            }
        } else {
            logger.error("全局配置参数未加载，请刷新全局配置参数或重新启动应用系统！");
        }
        return value;
    }

    public static boolean containsKey(String properKey) {
        if (configMap != null) {
            return configMap.containsKey(properKey);
        } else {
            logger.error("全局配置参数未加载，请刷新全局配置参数或重新启动应用系统！");
            return false;
        }
    }

	/*
    public static void main(String[] args) {
		SysParam sysParam = new SysParam();
		SysParam.setSysPath("D:\\Eclipse3.3\\workspace\\dragonspace\\SOAkage_CFGL\\web\\");
		SysParam.init();
		logger.info("" + SysParam.getConfigMapValue("cfgl.init"));
	}*/

    public static void main(String[] a) {
        ConfigContext.instancesBean(SysProp.class);
        ServiceContext serviceContext = new ServiceContext("com.sunsharing");
        ServiceLocation.getInstance().synConnect();
        for (int i = 0; i < 1000; i++) {
//            TestFirst testFirst = ServiceContext.getBean(TestFirst.class);
//            String out = testFirst.sayHello("hexin");
//            System.out.println(out);
        }
    }

}
