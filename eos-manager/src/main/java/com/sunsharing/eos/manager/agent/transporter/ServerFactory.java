/**
 * @(#)ServerFactory
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午2:08
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.transporter;

import com.sunsharing.eos.common.rpc.RpcServer;
import com.sunsharing.eos.manager.agent.transporter.netty.NettyAgentServer;
import com.sunsharing.eos.manager.agent.transporter.socket.SocketAgentServer;
import com.sunsharing.eos.manager.sys.SysProp;

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
public class ServerFactory {
    static Map<String, AbstractAgentServer> serverMap = new HashMap<String, AbstractAgentServer>();

    public static AbstractAgentServer getServer(String transporter) {
        AbstractAgentServer server = serverMap.get(transporter);
        if (server == null) {
            if ("netty".equals(transporter)) {
                server = new NettyAgentServer(SysProp.nettyServerPort);
            } else if ("socket".equals(transporter)) {
                server = new SocketAgentServer(SysProp.socketServerPort);
            } else throw new RuntimeException("没有该transporter的实现Server:" + transporter);
        }
        return server;
    }
}

