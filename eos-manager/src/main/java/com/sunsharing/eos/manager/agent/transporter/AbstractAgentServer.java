/**
 * @(#)AbstractAgentServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午2:40
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.transporter;

import com.sunsharing.eos.common.config.ServiceConfig;

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
public abstract class AbstractAgentServer {

    //服务器端口
    protected int port = 0;
    //是否运行中
    protected boolean isRunning = false;
    //存储服务
    protected Map<String, Object> serviceEngine = new HashMap<String, Object>();
    protected Map<String, ServiceConfig> serviceConfigEngine = new HashMap<String, ServiceConfig>();

    public AbstractAgentServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public abstract void stop();

    public abstract void start();
}

