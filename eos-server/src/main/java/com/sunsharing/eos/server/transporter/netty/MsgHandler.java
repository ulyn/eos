package com.sunsharing.eos.server.transporter.netty;

import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

/**
 * Created with IntelliJ IDEA.
 * User: ulyn
 * Date: 13-7-12
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
public class MsgHandler extends SimpleChannelHandler {
    private static final Logger logger = Logger.getLogger(MsgHandler.class);


    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
//        BasePro basePro = (BasePro)e.getMessage();
//        basePro.handler(ctx.getChannel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("处理异常", e.getCause());
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("channelConnected");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        //logger.debug("channelClosed");
        //删除通道
        //XLServer.allChannels.remove(e.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        //logger.debug("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.debug("channelOpen");
        //增加通道
        //XLServer.allChannels.add(e.getChannel());
    }
}
