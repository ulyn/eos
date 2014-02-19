/**
 * @(#)ClientHandler
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 上午10:32
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.remoting.netty;

import com.sunsharing.eos.common.rpc.protocol.BaseProtocol;
import com.sunsharing.eos.common.rpc.protocol.HeartPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.ClientCache;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.LongChannel;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.MyChannel;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.util.concurrent.ArrayBlockingQueue;

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
public class ClientHandler extends SimpleChannelHandler {
    private Logger logger = Logger.getLogger(ClientHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        BaseProtocol basePro = (BaseProtocol) e.getMessage();
        if(basePro instanceof HeartPro)
        {
            logger.debug("收到心跳");
            MyChannel ch = ClientCache.getChannel(e.getChannel());
            if(ch==null)
            {
                logger.warn("连接失去了没法更新心跳");
            }else
            {
                ((LongChannel)ch).refreshHeartBeat();
            }
        }else if(basePro instanceof ResponsePro)
        {
            logger.debug("收到结果");
            ResponsePro res = (ResponsePro)basePro;
            ArrayBlockingQueue<ResponsePro> queue = NettyClient.result.get(basePro.getMsgId());
            if (queue != null) {
                queue.add(res);
            }
            //ctx.getChannel().close();
            MyChannel ch = ClientCache.getChannel(e.getChannel());
            if(ch==null)
            {
                logger.info("疑似短连接，关闭");
                ctx.getChannel().close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("处理异常", e.getCause());
        e.getCause().printStackTrace();
        MyChannel ch = ClientCache.getChannel(e.getChannel());
        if(ch==null)
        {
            logger.info("疑似短连接，关闭");
            ctx.getChannel().close();
        }
    }
}

