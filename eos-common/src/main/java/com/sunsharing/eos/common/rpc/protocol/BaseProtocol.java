/**
 * @(#)BaseProtocol
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-2 下午8:09
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc.protocol;

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

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
public abstract class BaseProtocol {
    protected static byte REQUEST_MSG = 'A';
    protected static byte REQUEST_MSG_RESULT = 'B';

    //协议类型,请求还是响应(1)
    protected byte action;
    //消息id(32)
    protected String msgId;
    //序列化方式(15)
    protected String serialization = Constants.DEFAULT_SERIALIZATION;
    //报文体长度(4)
    protected int bodyLength;

    protected byte[] getHeaderBytes() {
        byte[] header = new byte[51];
        header[0] = action;
        StringUtils.putString(header, msgId, 1);
        StringUtils.putString(header, serialization, 33);
        StringUtils.putInt(header, getRealBodyLength(), 48);
        return header;
    }

    protected void setHeader(BaseProtocol pro, ChannelBuffer buffer) {
        pro.action = buffer.readByte();
        pro.msgId = readString(32, buffer);
        pro.serialization = readString(15, buffer);
        pro.bodyLength = buffer.readInt();
    }

    public byte getAction() {
        return action;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    protected String readString(int len, ChannelBuffer buffer) {
        byte[] bu = new byte[len];
        buffer.readBytes(bu);
        try {
            String s = new String(bu, "UTF-8").trim();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected byte[] getSerializationBytes(Object o) throws Exception {
        Serialization serial = SerializationFactory.createSerialization(serialization);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serial.serialize(outputStream);
        objectOutput.writeObject(o);
        objectOutput.flushBuffer();
        return outputStream.toByteArray();
    }

    protected <T> T serializationBytesToObject(byte[] bytes, Class<T> cls) throws Exception {
        Serialization serial = SerializationFactory.createSerialization(serialization);
        InputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = serial.deserialize(inputStream);
        return objectInput.readObject(cls);
    }


    protected abstract int getRealBodyLength();

    public abstract ChannelBuffer generate();

    public abstract BaseProtocol createFromChannel(ChannelBuffer buffer);

}

