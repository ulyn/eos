/**
 * @(#)AbstractServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午5:05
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.server.server;

import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.Result;
import com.sunsharing.eos.common.rpc.Server;
import com.sunsharing.eos.common.rpc.impl.RpcResult;

import java.lang.reflect.Method;
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
public abstract class AbstractServer implements Server {

    //服务器端口
    protected int port = 20382;
    //是否运行中
    protected boolean isRunning = false;
    //存储服务
    protected Map<String, Object> serviceEngine = new HashMap<String, Object>();
    protected Map<String, ServiceConfig> serviceConfigEngine = new HashMap<String, ServiceConfig>();

    public AbstractServer(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void register(Object impl, ServiceConfig config) {
        this.serviceEngine.put(config.getId(), impl);
        this.serviceConfigEngine.put(config.getId(), config);
        if (!isRunning()) {
            this.start();
        }
        //往zookeeper注册服务
    }

    public Result call(Invocation invocation) {
        Object obj = this.serviceEngine.get(invocation.getId());
        ServiceConfig config = this.serviceConfigEngine.get(invocation.getId());
        RpcResult result = new RpcResult();
        if (obj != null) {
            //这边暂时直接使用jdk代理执行
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

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}

