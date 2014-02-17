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
import com.sunsharing.component.resvalidate.config.annotation.validate.IpValidate;
import com.sunsharing.component.resvalidate.config.annotation.validate.NumValidate;

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
public class SysProp {

    @ParamField(name = "zookeeper_ip", required = false)
    public static String zookeeperIp = "localhost";

    @ParamField(name = "zookeeper_port", required = false)
    @NumValidate
    public static int zookeeperPort = 2181;

    @ParamField(name = "debugging_server_ip", required = false)
    @IpValidate(allowEmpty = true)
    public static String debugServerIp = "";

    @ParamField(name = "use_mock", required = false)
    public static boolean use_mock = false;
}

