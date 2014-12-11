/**
 * @(#)StubRpc
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-12-8 下午9:54
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.eos.client.ServiceContext;
import com.sunsharing.eos.client.proxy.AbstractProxy;
import com.sunsharing.eos.client.proxy.ProxyFactory;
import com.sunsharing.eos.client.sys.SysParamVar;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.config.ServiceMethod;
import com.sunsharing.eos.common.rpc.RpcContext;
import com.sunsharing.eos.common.rpc.RpcContextContainer;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 根据本地服务存根创建远程服务调用操作
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class StubRpc {
    Logger logger = Logger.getLogger(StubRpc.class);

    public Object doInvoke(RpcInvocation invocation, ServiceConfig config) throws RpcException {
        if (logger.isDebugEnabled()) {
            logger.debug("调用eos服务" + config.getId() + "入参：" + invocation);
        }
        if (StringUtils.isBlank(config.getAppId())) {
            throw new RpcException(RpcException.REFLECT_INVOKE_EXCEPTION, "接口" + config.getId() + "不正确,没有appid,请确保是从eos下载");
        }

        ServiceMethod serviceMethod = config.getMethod(invocation.getMethodName());
        Class retType = serviceMethod.getRetType();
        String mock = invocation.getRealMock(config, SysProp.use_mock, retType);

        DynamicRpc dynamicRpc = DynamicRpc.create(config.getAppId(), config.getId(), config.getVersion())
                .setSerialization(config.getSerialization())
                .setTimeout(config.getTimeout())
                .setTransporter(config.getTransporter())
                .setMock(mock);
        return dynamicRpc.doInvoke(retType, invocation.getMethodName(), invocation.getArguments());
    }
}

