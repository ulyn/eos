/**
 * @(#)ResponsePro
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-4 下午11:56
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
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 服务端返回协议
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class ResponsePro extends BaseProtocol {
    /**
     * status 0 正常 1异常
     */
    byte status = 0;

    byte[] resultBytes;

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public byte[] getResultBytes() {
        return resultBytes;
    }

    public void setResultBytes(byte[] resultBytes) {
        this.resultBytes = resultBytes;
    }

    public void setResult(Result result) throws Exception {
        if (result.hasException()) {
            status = Constants.STATUS_ERROR;
        }
        setResultBytes(getSerializationBytes(result));
    }

    public Result toResult() throws Exception {
        if (resultBytes == null || resultBytes.length == 0) {
            return null;
        }
        Result result = serializationBytesToObject(resultBytes, Result.class);
        return result;
    }

    @Override
    protected int getRealBodyLength() {
        if (resultBytes == null) {
            return 0;
        } else return resultBytes.length;
    }

    @Override
    public ChannelBuffer generate() {
        setAction(REQUEST_MSG_RESULT);

        ChannelBuffer buffer = ChannelBuffers.dynamicBuffer();
        buffer.writeBytes(getHeaderBytes());
        buffer.writeByte(status);
        if (resultBytes != null) {
            buffer.writeBytes(resultBytes);
        }
        return buffer;
    }

    @Override
    public BaseProtocol createFromChannel(ChannelBuffer buffer) {
        if (buffer.readableBytes() < 53) {
            return null;
        }
        buffer.markReaderIndex();

        ResponsePro pro = new ResponsePro();
        setHeader(pro, buffer);
        pro.setStatus(buffer.readByte());

        if (buffer.readableBytes() < pro.bodyLength) {
            buffer.resetReaderIndex();
            return null;
        }
        byte[] bodyBytes = new byte[pro.bodyLength];
        buffer.readBytes(bodyBytes);
        pro.setResultBytes(bodyBytes);
        return pro;
    }
}

