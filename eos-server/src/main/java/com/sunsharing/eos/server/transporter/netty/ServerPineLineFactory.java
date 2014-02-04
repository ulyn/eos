package com.sunsharing.eos.server.transporter.netty;

import com.sunsharing.eos.common.rpc.server.netty.ExDecode;
import com.sunsharing.eos.common.rpc.server.netty.ExEncode;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * Created with IntelliJ IDEA.
 * User: criss
 * Date: 13-7-22
 * Time: 下午6:00
 * To change this template use File | Settings | File Templates.
 */
public class ServerPineLineFactory implements ChannelPipelineFactory {
    public ChannelPipeline getPipeline() throws Exception {
        // Create a default pipeline implementation.
        ChannelPipeline pipeline = pipeline();
        pipeline.addLast("decoder", new ExDecode());
        pipeline.addLast("encoder", new ExEncode());
        pipeline.addLast("handler", new MsgHandler());
        return pipeline;
    }
}

