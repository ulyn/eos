/**
 * @(#)ServiceServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午2:03
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent;

import com.sunsharing.eos.manager.agent.transporter.AbstractAgentServer;
import com.sunsharing.eos.manager.agent.transporter.ServerFactory;

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
public class ServiceServer {

    /**
     * 启动注册的服务代理服务器
     *
     * @return
     */
    public static boolean startUp() {
        AbstractAgentServer server = ServerFactory.getServer("netty");
        server.start();
        return true;
    }
}

