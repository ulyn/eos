/**
 * @(#)NettyAgentServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午2:23
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.transporter.netty;

import com.sunsharing.eos.common.rpc.remoting.netty.ExDecode;
import com.sunsharing.eos.common.rpc.remoting.netty.ExEncode;
import com.sunsharing.eos.common.rpc.remoting.netty.NettyServer;
import com.sunsharing.eos.manager.agent.transporter.AbstractAgentServer;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import java.util.HashMap;
import java.util.Map;

import static org.jboss.netty.channel.Channels.pipeline;

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
public class NettyAgentServer extends AbstractAgentServer {

    Logger logger = Logger.getLogger(NettyAgentServer.class);
    NettyServer nettyServer;

    public NettyAgentServer(int port) {
        super(port);
    }


    @Override
    public void stop() {
        setRunning(false);
        if (nettyServer != null) {
            try {
                nettyServer.shutdown();
            } catch (Exception e) {
                logger.error("关闭netty服务器失败！", e);
            }
        }
    }

    @Override
    public void start() {
        setRunning(true);
        nettyServer = new NettyServer(this.port, new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = pipeline();
                pipeline.addLast("decoder", new ExDecode());
                pipeline.addLast("encoder", new ExEncode());
                pipeline.addLast("handler", new MsgHandler());
                return pipeline;
            }
        });
        try {
            nettyServer.startup();
        } catch (Exception e) {
            logger.error("启动netty服务器失败！", e);
        }

    }
}

