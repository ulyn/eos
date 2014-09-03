package com.sunsharing.eos.manager.agent.transporter.netty;

import com.sunsharing.eos.common.rpc.protocol.BaseProtocol;
import com.sunsharing.eos.common.rpc.protocol.HeartPro;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.netty.channel.*;
import com.sunsharing.eos.manager.agent.process.MainControl;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: ulyn
 * Date: 13-7-12
 * Time: 下午5:56
 * To change this template use File | Settings | File Templates.
 */
public class MsgHandler extends SimpleChannelHandler {
    private static final Logger logger = Logger.getLogger(MsgHandler.class);
    static ExecutorService service =  Executors.newCachedThreadPool();

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        final BaseProtocol basePro = (BaseProtocol) e.getMessage();
        final ChannelHandlerContext content = ctx;
        if(basePro instanceof HeartPro)
        {
            logger.info("收到心跳请求...");
            com.sunsharing.eos.common.rpc.remoting.netty.channel.ServerChannel sc = ServerCache.getChannel(ctx.getChannel());
            if(sc!=null)
            {
                sc.refreshHeartBeat();
            }else
            {
                logger.warn("无法从缓存中找到Channel:"+ctx.getChannel().getRemoteAddress());
            }
            HeartPro heart = new HeartPro();
            ctx.getChannel().write(heart);
        }else if(basePro instanceof RequestPro)
        {
            logger.info("收到请求：" + basePro);
            service.execute(new Runnable() {
                @Override
                public void run() {
                    RequestPro req = (RequestPro)basePro;
                    ResponsePro responsePro = new ResponsePro();
                    responsePro.setSerialization(basePro.getSerialization());
                    responsePro.setMsgId(basePro.getMsgId());

                    MainControl mainControl = new MainControl();
                    mainControl.process(req, responsePro);
                    content.getChannel().write(responsePro);
                }
            });

        }



    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("处理异常", e.getCause());
        e.getCause().printStackTrace();
        //e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("channelConnected");

    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("channelClosed");
        //删除通道
        //XLServer.allChannels.remove(e.getChannel());
        ServerCache.removeChannel(ctx.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.info("channelOpen");
        //增加通道
        //XLServer.allChannels.add(e.getChannel());
        ServerCache.addChannel(ctx.getChannel());
    }
}
