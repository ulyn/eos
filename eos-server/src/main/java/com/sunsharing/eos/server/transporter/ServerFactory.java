/**
 * @(#)ServerFactory
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午10:16
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.server.transporter;

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.RpcServer;
import com.sunsharing.eos.server.sys.EosServerProp;
import org.apache.log4j.Logger;

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
    static Logger logger = Logger.getLogger(ServerFactory.class);
    static Map<String, RpcServer> serverMap = new HashMap<String, RpcServer>();

    public static RpcServer getServer(String transporter) {
        RpcServer server = serverMap.get(transporter);
        if (server == null) {
            logger.info("server为空,重新New");
            if (Constants.SHORT_NETTY.equals(transporter)) {
                server = new NettyRpcServer(EosServerProp.nettyServerPort);
                serverMap.put(transporter,server);
            } else if (Constants.LONG_NETTY.equals(transporter))
            {
                server = new NettyRpcServer(EosServerProp.nettyServerPort);
                serverMap.put(transporter,server);
            }else
            {
                throw new RuntimeException("没有该transporter的实现Server:" + transporter);
            }
        }
        return server;
    }
}

