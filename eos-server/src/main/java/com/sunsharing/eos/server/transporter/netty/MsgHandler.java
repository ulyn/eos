package com.sunsharing.eos.server.transporter.netty;

import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.RpcServer;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.BaseProtocol;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
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

    RpcServer rpcServer;

    public MsgHandler(RpcServer server) {
        this.rpcServer = server;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        RequestPro basePro = (RequestPro) e.getMessage();
        logger.debug("收到请求：" + basePro);
//        basePro.handler(ctx.getChannel());

        Result result = rpcServer.call(basePro.toInvocation());

        ResponsePro responsePro = new ResponsePro();
        responsePro.setSerialization(basePro.getSerialization());
        responsePro.setMsgId(basePro.getMsgId());
        responsePro.setResult(result);
        ctx.getChannel().write(responsePro);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("处理异常", e.getCause());
        e.getCause().printStackTrace();
        e.getChannel().close();
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//        logger.info("channelConnected");
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//        logger.info("channelClosed");
        //删除通道
        //XLServer.allChannels.remove(e.getChannel());
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//        logger.info("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }

    @Override
    public void channelOpen(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
//        logger.info("channelOpen");
        //增加通道
        //XLServer.allChannels.add(e.getChannel());
    }
}
