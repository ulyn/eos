package com.sunsharing.eos.common.rpc.protocol;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * Created by criss on 14-2-19.
 */
public class HeartPro extends BaseProtocol {

    @Override
    public ChannelBuffer generate() {
        setAction(HEART_BEAT);
        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        byte[] header = new byte[1];
        header[0] = HEART_BEAT;
        buffer.writeBytes(header);
        return buffer;
    }

    @Override
    public BaseProtocol createFromChannel(ChannelBuffer buffer) {
        if (buffer.readableBytes() < 1) {
            return null;
        }
        HeartPro heartPro = this;
        heartPro.action = buffer.readByte();
        return heartPro;
    }
}
