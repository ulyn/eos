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
package com.sunsharing.eos.server.transporter;

import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.exception.ExceptionHandler;
import com.sunsharing.eos.common.filter.FilterChain;
import com.sunsharing.eos.common.filter.FilterManager;
import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;
import com.sunsharing.eos.common.rpc.*;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.server.ServerServiceContext;
import com.sunsharing.eos.server.sys.SysProp;
import org.apache.log4j.Logger;

import java.io.IOException;
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
public abstract class AbstractServer implements RpcServer {
    Logger logger = Logger.getLogger(AbstractServer.class);
    //服务器端口
    protected int port = 20382;
    //是否运行中
    protected boolean isRunning = false;
    //存储服务
    protected Map<String, Object> serviceEngine = new HashMap<String, Object>();
    protected Map<String, ServiceConfig> serviceConfigEngine = new HashMap<String, ServiceConfig>();
    private ServiceInvokeFilter serviceInvokeFilter = null;

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
        if (serviceInvokeFilter == null) {
            serviceInvokeFilter = new ServiceInvokeFilter(this.serviceEngine, this.serviceConfigEngine);
        }
        //往zookeeper注册服务，已经不需要了，直接写在ServiceConnectCallBack
    }

    @Override
    public ResponsePro callService(RequestPro requestPro) {
        ServiceResponse response = null;
        ServiceRequest request = null;
        try {
            request = ServiceRequest.createServiceRequest(requestPro);
            //设置rpc上下文 ---- 封装成serviceRequest后，server基本不需要设置这个上下文了。此处预留兼容
            //上下文消息使用 request.getAttribute获取
            RpcContextContainer.setRpcContext(request.createRpcContext());
        } catch (IOException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RpcException(RpcException.SERIALIZATION_EXCEPTION, e.getMessage(), e);
        }
        response = new ServiceResponse(request);
        try {
            FilterChain filterChain = FilterManager.createFilterChain(SysProp.appId, requestPro.getServiceId());
            filterChain.addFilter(serviceInvokeFilter);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.writeError(e);
        }
        //尝试处理全局异常
        ExceptionHandler.tryHandleException(request, response, ServerServiceContext.getExceptionResolver());

        ResponsePro responsePro = response.toResponsePro();
        responsePro.setMsgId(requestPro.getMsgId());
        return responsePro;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}

