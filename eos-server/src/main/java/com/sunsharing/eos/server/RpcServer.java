/**
 * @(#)RpcServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-26 下午4:37
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.server;

import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.Server;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.serialize.*;
import com.sunsharing.eos.common.serialize.support.hessian.Hessian2Serialization;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

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
public class RpcServer implements Server {
    private int port = 20382;

    private boolean isRunning = false;
    private Map<String, Object> serviceEngine = new HashMap<String, Object>();

    @Override
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void register(Class interfaceDefiner, Object impl) {
        try {
            this.serviceEngine.put(impl.getClass().getName(), impl);
            System.out.println(serviceEngine);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result call(Invocation invocation) {
        Object obj = serviceEngine.get(invocation.getImplClassName());
        RpcResult result = new RpcResult();
        if (obj != null) {
            try {
                Method m = obj.getClass().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                Object o = m.invoke(obj, invocation.getArguments());
                result.setValue(o);
            } catch (Throwable th) {
                result.setException(th);
            }
        } else {
            result.setException(new IllegalArgumentException("has no these class"));
        }
        return result;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
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
//                        ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
//                        Invocation invo = (Invocation) ois.readObject();
                        Serialization serialization = new Hessian2Serialization();
                        ObjectInput objectInput = serialization.deserialize(client.getInputStream());
                        Invocation invo = objectInput.readObject(Invocation.class);
                        System.out.println("socket 读取参数对象耗时:" + (System.currentTimeMillis() - startTime));
                        System.out.println("远程调用:" + invo.getImplClassName() + "." + invo.getMethodName());

                        startTime = System.currentTimeMillis();
                        Result result = call(invo);
                        long endTime = System.currentTimeMillis();
                        System.out.println("执行调用耗时:" + (endTime - startTime) + "毫秒");

                        startTime = System.currentTimeMillis();
                        ObjectOutput objectOutput = serialization.serialize(client.getOutputStream());
                        objectOutput.writeObject(result);
                        objectOutput.flushBuffer();
//                        ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
//                        oos.writeObject(result);
//                        oos.flush();
//                        oos.close();
//                        ois.close();
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

