/**
 * @(#)AbstractProxy
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午11:35
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.proxy;

import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.client.sys.SysProp;
import com.sunsharing.eos.client.zookeeper.ServiceLocation;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.aop.Advice;
import com.sunsharing.eos.common.aop.AdviceResult;
import com.sunsharing.eos.common.config.ServiceConfig;
import com.sunsharing.eos.common.config.ServiceMethod;
import com.sunsharing.eos.common.rpc.*;
import com.sunsharing.eos.common.rpc.impl.RpcInvocation;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.rpc.remoting.RemoteHelper;
import com.sunsharing.eos.common.utils.CompatibleTypeUtils;
import com.sunsharing.eos.common.utils.StringUtils;
import org.apache.log4j.Logger;

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
public abstract class AbstractProxy implements ClientProxy {
    Logger logger = Logger.getLogger(AbstractProxy.class);

    /**
     * 执行方法
     *
     * @param invocation
     * @param config
     * @return
     * @throws Throwable
     */
    public Object doInvoke(RpcInvocation invocation, ServiceConfig config) throws Throwable {
        ServiceMethod serviceMethod = config.getMethod(invocation.getMethodName());
        return doInvoke(invocation, config, serviceMethod);
    }

    /**
     * 执行方法
     *
     * @param invocation
     * @param config
     * @param serviceMethod
     * @return
     * @throws Throwable
     */
    public Object doInvoke(RpcInvocation invocation, ServiceConfig config, ServiceMethod serviceMethod) throws Throwable {
        logger.info("调用eos服务入参：" + invocation);
        Object o = null;
        Advice advice = serviceMethod.getAdvice();
        AdviceResult adviceResult = null;
        //执行调用前
        if (advice != null) {
            adviceResult = advice.before(serviceMethod, invocation.getArguments());
        }
        //执行没截断，开始执行调用
        if (adviceResult == null || !adviceResult.isRightNowRet()) {
            o = getRpcResult(invocation, config, serviceMethod);

            if (advice != null) {
                adviceResult = advice.after(serviceMethod, invocation.getArguments(), o);
                if (adviceResult != null) {
                    o = adviceResult.getReturnVal();
                }
            }
        } else {
            o = adviceResult.getReturnVal();
        }
        return o;
    }

    /**
     * 执行远程调用
     *
     * @param invocation
     * @param config
     * @param serviceMethod
     * @return
     * @throws Throwable
     */
    private Object getRpcResult(RpcInvocation invocation, ServiceConfig config, ServiceMethod serviceMethod) throws Throwable {

        if (StringUtils.isBlank(config.getAppId())) {
            throw new RpcException(RpcException.REFLECT_INVOKE_EXCEPTION, "接口" + config.getId() + "不正确,没有appid,请确保是从eos下载");
        }
        Class retType = serviceMethod.getRetType();
        String mock = invocation.getRealMock(config, SysProp.use_mock, retType);
        boolean isMock = !StringUtils.isBlank(mock);
        //zookeeper取得服务的ip
        JSONObject jo;
        if (!isMock) {
            jo = ServiceLocation.getInstance().getServiceLocation(config.getAppId(), config.getId(), config.getVersion());
        } else {
            jo = ServiceLocation.getInstance().getOnlineEOS();
        }
        if (jo == null) {
            if (isMock) {
                logger.error("对于模拟调用，没有找到可用的eos,请确保服务" + config.getAppId() + "-"
                        + config.getId() + "-"
                        + config.getVersion() + "是否有效或者eos节点已经启动！");
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "对于模拟调用，没有找到可用的eos,请确保服务" + config.getAppId() + "-"
                        + config.getId() + "-"
                        + config.getVersion() + "是否有效或者eos节点已经启动！");
            } else {
                logger.error("没有找到请求的可用的eos节点,请确保服务" + config.getAppId() + "-"
                        + config.getId() + "-"
                        + config.getVersion() + "是否有效或者eos节点已经启动！");
                throw new RpcException(RpcException.SERVICE_NO_FOUND_EXCEPTION, "没有找到请求的可用的eos节点,请确保服务" + config.getAppId() + "-"
                        + config.getId() + "-"
                        + config.getVersion() + "是否有效或者eos节点已经启动！");
            }
        }

        String ip = jo.getString("ip");
        int port = jo.getInteger("port");

        RequestPro pro = new RequestPro();
        pro.setAppId(config.getAppId());
        pro.setServiceId(config.getId());
        pro.setServiceVersion(config.getVersion());
        pro.setSerialization(config.getSerialization());
        if (isMock) {
            pro.setMock(mock);
            logger.info(pro.getServiceId() + "." + invocation.getMethodName() + " use mock:" + pro.getMock());
        }
        pro.setInvocation(invocation);
        pro.setDebugServerIp(SysProp.getDebugServerIp(pro.getAppId()));

        //增加rpcContext
        RpcContext rpcContext = RpcContextContainer.getRpcContext();
        if (rpcContext == null) {
            rpcContext = new RpcContext();
            rpcContext.setUserAgent("java");
        }
        pro.setRpcContext(rpcContext);

        RemoteHelper helper = new RemoteHelper();
        ResponsePro responsePro = helper.call(pro, ip, port, config.getTransporter(), config.getTimeout());

        return getResult(pro, isMock, retType, responsePro);
    }

    /**
     * 转换ResponsePro获取返回结果
     *
     * @param pro
     * @param isMock
     * @param retType
     * @param responsePro
     * @return
     * @throws Throwable
     */
    private Object getResult(RequestPro pro, boolean isMock, Class retType, ResponsePro responsePro) throws Throwable {
        Result rpcResult = responsePro.toResult();
        if (responsePro.getStatus() == Constants.STATUS_ERROR) {
            if (rpcResult.hasException()) {
                throw rpcResult.getException();
            } else {
                String error = "服务调用失败！"
                        + pro.getAppId() + "-"
                        + pro.getServiceId() + "-"
                        + pro.getServiceVersion();
                throw new RpcException(RpcException.UNKNOWN_EXCEPTION, error);
            }
        } else {
            Object value = rpcResult.getValue();
            if (isMock) {
                if (void.class == retType || value == null) {
                    return null;
                }
                //返回的类型一样，则不需要进行转换，返回类型是string或者不是模拟返回
                //返回的类型不一样，则需要进行转换
//                if (value.getClass() != retType) {
                if (value instanceof String && !retType.isInstance(value)) {
                    value = CompatibleTypeUtils.compatibleTypeConvert((String) value, retType);
                }
            }
            return value;
        }
    }
}

