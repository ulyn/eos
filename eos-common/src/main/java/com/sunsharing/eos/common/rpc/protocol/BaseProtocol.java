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
import com.sunsharing.eos.common.utils.StringUtils;
import org.jboss.netty.buffer.ChannelBuffer;

import java.io.Serializable;

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
public abstract class BaseProtocol implements Serializable {
    public static byte REQUEST_MSG = 'A';
    public static byte REQUEST_MSG_RESULT = 'B';
    public static byte HEART_BEAT = 'H';

    //协议类型,请求还是响应(1)
    protected byte action;
    //EOS版本号(5)
    protected String eosVersion;
    //消息id(32)
    protected String msgId;
    //序列化方式(10)
    protected String serialization = Constants.DEFAULT_SERIALIZATION;

    protected byte[] getHeaderBytes() {
        byte[] header = new byte[48];
        header[0] = action;
        StringUtils.putString(header, eosVersion, 1);
        StringUtils.putString(header, msgId, 6);
        StringUtils.putString(header, serialization, 38);
        return header;
    }

    protected void setHeader(BaseProtocol pro, ChannelBuffer buffer) {
        pro.action = buffer.readByte();
        pro.eosVersion = readString(5, buffer);
        pro.msgId = readString(32, buffer);
        pro.serialization = readString(10, buffer);
    }

    public byte getAction() {
        return action;
    }

    public void setAction(byte action) {
        this.action = action;
    }

    public String getEosVersion() {
        return eosVersion;
    }

    public void setEosVersion(String eosVersion) {
        this.eosVersion = eosVersion;
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

    public abstract ChannelBuffer generate();

    public abstract BaseProtocol createFromChannel(ChannelBuffer buffer);

//    public static void main(String[] a) throws Exception {
//        Map o = new HashMap();
//        o.put("a", new BigDecimal("11.111"));
//        o.put("b", new Date());
//
//        Serialization serial = SerializationFactory.createSerialization("hessian");
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ObjectOutput objectOutput = serial.serialize(outputStream);
//        objectOutput.writeObject(o);
//        objectOutput.flushBuffer();
//
//
//        byte[] bytes = outputStream.toByteArray();
//
//        InputStream inputStream = new ByteArrayInputStream(bytes);
//        ObjectInput objectInput = serial.deserialize(inputStream);
//        Object m = objectInput.readObject(Map.class);
//        Object ss = new HashMap();
////        System.out.println(m.get("a"));
////        System.out.println(m.get("b"));
//        System.out.println(m.getClass());
//        System.out.println(ss instanceof String);
//        System.out.println(Map.class.isInstance(m));
//        System.out.println(String.class.isInstance("sss"));
//
//
//    }
}

