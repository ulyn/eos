package com.sunsharing.eos.common.rpc.server.netty;

import com.sunsharing.eos.common.rpc.protocol.BaseProtocol;
import org.apache.log4j.Logger;
import org.jboss.netty.channel.*;

import static org.jboss.netty.channel.Channels.write;

/**
 * Created with IntelliJ IDEA.
 * User: criss
 * Date: 13-7-12
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class ExEncode implements ChannelDownstreamHandler {
    Logger logger = Logger.getLogger(ExEncode.class);

    public void handleDownstream(
            ChannelHandlerContext ctx, ChannelEvent evt) throws Exception {
        if (!(evt instanceof MessageEvent)) {
            ctx.sendDownstream(evt);
            return;
        }

        MessageEvent e = (MessageEvent) evt;
        Object originalMessage = e.getMessage();
        Object encodedMessage = encode(ctx, e.getChannel(), originalMessage);
        if (originalMessage == encodedMessage) {
            ctx.sendDownstream(evt);
        } else if (encodedMessage != null) {
            write(ctx, e.getFuture(), encodedMessage, e.getRemoteAddress());
        }
    }

    protected Object encode(
            ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        logger.info("encode:" + msg);
        if (msg instanceof BaseProtocol) {
            BaseProtocol pro = (BaseProtocol) msg;
            return pro.generate();
        } else {
            return msg;
        }
    }
}
