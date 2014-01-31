/**
 * @(#)SocketClient
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午11:41
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

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
public class SocketClient extends AbstractClient {

    @Override
    public RpcResult doRpc(Invocation invocation,
                           String serializationType, String url, int port) throws Throwable {
        Socket socket = null;
        socket = new Socket(url, port);
        long startTime = System.currentTimeMillis();
        OutputStream outputStream = socket.getOutputStream();
        byte[] bytes = new byte[20];
        StringUtils.putString(bytes, serializationType, 0);
        outputStream.write(bytes);

        Serialization serialization = SerializationFactory.createSerialization(serializationType);
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        objectOutput.writeObject(invocation);
        objectOutput.flushBuffer();
//                oos.flush();
        System.out.println("socket 打开并写入参数耗时:" + (System.currentTimeMillis() - startTime));
//                ois = new ObjectInputStream(socket.getInputStream());
        ObjectInput objectInput = serialization.deserialize(socket.getInputStream());
        startTime = System.currentTimeMillis();
//                RpcResult result = (RpcResult) ois.readObject();
        RpcResult result = objectInput.readObject(RpcResult.class);
        System.out.println("socket 读回结果耗时:" + (System.currentTimeMillis() - startTime));
        return result;
    }
}

