/**
 * @(#)RpcClient
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-26 下午4:28
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client;

import com.sunsharing.eos.common.rpc.Client;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.serialize.ObjectInput;
import com.sunsharing.eos.common.serialize.ObjectOutput;
import com.sunsharing.eos.common.serialize.Serialization;
import com.sunsharing.eos.common.serialize.support.hessian.Hessian2Serialization;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
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
public class RpcClient implements Client {
    @Override
    public <T> T getProxy(final Class<T> clazz, final String implClassName) throws RpcException {
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                RpcInvocation invo = new RpcInvocation();
                invo.setInterfaces(clazz.getName());
                invo.setImplClassName(implClassName);
                invo.setMethodName(method.getName());
                invo.setParameterTypes(method.getParameterTypes());
                invo.setArguments(args);


                //可以抽象出来
                Socket socket = new Socket("localhost", 20382);
                long startTime = System.currentTimeMillis();
//                ObjectOutputStream oos;
//                ObjectInputStream ois;
                Serialization serialization = new Hessian2Serialization();
//                oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectOutput objectOutput = serialization.serialize(socket.getOutputStream());
                objectOutput.writeObject(invo);
                objectOutput.flushBuffer();
//                oos.flush();
                System.out.println("socket 打开并写入参数耗时:" + (System.currentTimeMillis() - startTime));
//                ois = new ObjectInputStream(socket.getInputStream());
                ObjectInput objectInput = serialization.deserialize(socket.getInputStream());
                startTime = System.currentTimeMillis();
//                RpcResult result = (RpcResult) ois.readObject();
                RpcResult result = objectInput.readObject(RpcResult.class);
                System.out.println("socket 读回结果耗时:" + (System.currentTimeMillis() - startTime));
                if (result.hasException()) {
                    throw result.getException();
                }
                return result.getValue();
            }
        };
        T t = (T) Proxy.newProxyInstance(RpcClient.class.getClassLoader(), new Class[]{clazz}, handler);
        return t;
    }
}

