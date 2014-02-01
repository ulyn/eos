/**
 * @(#)SocketServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午10:49
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.server.transporter;

import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.SerializationFactory;
import com.sunsharing.eos.common.utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
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
public class SocketServer extends AbstractServer {

    public SocketServer(int port) {
        super(port);
    }

    @Override
    public void stop() {
        this.setRunning(false);
    }

    @Override
    public void start() {
        System.out.println("启动服务器");
        this.setRunning(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket socket;
                System.out.println("启动服务器中，打开端口" + getPort());
                try {
                    socket = new ServerSocket(getPort());
                } catch (IOException e1) {
                    e1.printStackTrace();
                    return;
                }
                while (isRunning()) {
                    try {
                        Socket client = socket.accept();
                        long startTime = System.currentTimeMillis();
                        InputStream inputStream = client.getInputStream();
                        byte[] bytes = new byte[20];
                        inputStream.read(bytes);
                        String serializationType = StringUtils.getString(bytes, 20, 0);

                        Serialization serialization = SerializationFactory.createSerialization(serializationType);
                        ObjectInput objectInput = serialization.deserialize(client.getInputStream());
                        Invocation invo = objectInput.readObject(Invocation.class);
                        System.out.println("socket 读取参数对象耗时:" + (System.currentTimeMillis() - startTime));
                        System.out.println("远程调用:" + serviceEngine.get(invo.getId()) + "." + invo.getMethodName());

                        startTime = System.currentTimeMillis();
                        Result result = call(invo);
                        long endTime = System.currentTimeMillis();
                        System.out.println("执行调用耗时:" + (endTime - startTime) + "毫秒");

                        startTime = System.currentTimeMillis();
                        ObjectOutput objectOutput = serialization.serialize(client.getOutputStream());
                        objectOutput.writeObject(result);
                        objectOutput.flushBuffer();
                        System.out.println("socket 写回数据耗时:" + (System.currentTimeMillis() - startTime));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

