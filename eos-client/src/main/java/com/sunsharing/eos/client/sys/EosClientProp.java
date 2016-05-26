/**
 * @(#)SysProp
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-27 上午10:10
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.sys;

import com.sunsharing.component.resvalidate.config.annotation.Configuration;
import com.sunsharing.component.resvalidate.config.annotation.ParamField;
import com.sunsharing.component.resvalidate.config.annotation.validate.NumValidate;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 系统配置参数类，配置文件名为eos.properties。系统初始化时调用：
 * <br>  ConfigContext.instancesBean(SysProp.class);
 * <br> 注意事项:
 * <br>  使用前一定要先调用 ConfigContext.instancesBean(SysProp.class);否则得不到预期的值。
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
@Configuration(value = "eos-client.properties")
public class EosClientProp {

    private final static Logger logger = Logger.getLogger(EosClientProp.class);

    @ParamField(name = "zookeeper_ip", required = false)
    public static String zookeeperIp = "";

    @ParamField(name = "zookeeper_port", required = false)
    @NumValidate
    public static int zookeeperPort = 2181;

    @ParamField(name = "debugging_server_ip", required = false)
//    @IpValidate(allowEmpty = true)
    public static String debugServerIp = "";

    @ParamField(name = "use_mock", required = false)
    public static boolean use_mock = false;

    @ParamField(name = "eos_filter", required = false)
    public static String eosFilter = "";

    @ParamField(name = "app_id", required = false)
    public static String app_id = "";


    static Map<String, String> debugServerIpMap = null;

    /**
     * 获取联调ip
     *
     * @return
     */
    public static String getDebugServerIp(String appId) {
        if (StringUtils.isBlank(appId)) {
            return "";
        } else {
            if (debugServerIpMap == null) {
                //初始化debugServerIpMap
                logger.info("初始化debugServerIpMap.....");
                logger.info("debugServerIp = " + debugServerIp);
                debugServerIpMap = new HashMap<String, String>();
                String[] ipStrArr = debugServerIp.split(";");
                for (String ipStr : ipStrArr) {
                    if (ipStr.indexOf(":") != -1) {
                        String[] temp = ipStr.split(":");
                        logger.info("应用：" + temp[0] + "，使用联调ip：" + temp[1]);
                        debugServerIpMap.put(temp[0], temp[1]);
                    } else {
                        debugServerIpMap.put("defaultDebugServerIp", ipStr);
                    }
                }
                logger.info("初始化debugServerIpMap结束.....");
            }

            if (debugServerIpMap.containsKey(appId)) {
                return debugServerIpMap.get(appId);
            } else if (debugServerIpMap.containsKey("defaultDebugServerIp")) {
                return debugServerIpMap.get("defaultDebugServerIp");
            } else {
                return "";
            }
        }
    }

}

