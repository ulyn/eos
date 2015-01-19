/**
 * @(#)MockProcess
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-6 下午11:50
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.process;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.Invocation;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.common.utils.StringUtils;
import com.sunsharing.eos.manager.sys.SysProp;
import com.sunsharing.eos.manager.zookeeper.ServiceCache;
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
public class MockProcess implements Process {
    Logger logger = Logger.getLogger(MockProcess.class);

    @Override
    public void doProcess(RequestPro req, ResponsePro res, ProcessChain processChain) {

        if (SysProp.eosMode.equalsIgnoreCase(Constants.EOS_MODE_DEV)
                && !StringUtils.isBlank(req.getMock())) {
            //开发模式，支持模拟
            String methodName = null;
            try {
                Invocation invocation = req.toInvocation();
                methodName = invocation.getMethodName();
//                retType = invocation.getRetType();
            } catch (Exception e) {
                String error = "反序列化服务Invocation失败！";
                logger.error(error, e);
                res.setExceptionResult(new RpcException(RpcException.SERIALIZATION_EXCEPTION, error));
            }
            if (methodName != null) {
                try {
                    JSONArray array = ServiceCache.getInstance().getTestCode(req.getAppId(), req.getServiceId(), req.getServiceVersion(), methodName);
                    boolean findMock = false;
                    if (array != null) {
                        for (int i = 0; i < array.size(); i++) {
                            JSONObject jo = array.getJSONObject(i);
                            if (req.getMock().equals(jo.getString("status"))) {
                                String content = jo.getString("content");
                                res.setResult(new RpcResult(content));
                                findMock = true;
                                break;
                            }
                        }
                    }
                    if (!findMock) {
                        String error = "服务接口" + req.getAppId() + "-"
                                + req.getServiceId() + "-"
                                + req.getServiceVersion() + "-"
                                + methodName + "没有配置指定的mock:" + req.getMock();
                        logger.error(error);
                        res.setExceptionResult(new RpcException(RpcException.MOCK_EXCEPTION, error));
                    }
                } catch (Exception e) {
                    String error = "获取模拟测试值异常！" + req.getAppId() + "-"
                            + req.getServiceId() + "-"
                            + req.getServiceVersion() + "-"
                            + methodName + "-"
                            + req.getMock();
                    logger.error(error, e);
                    res.setExceptionResult(new RpcException(RpcException.MOCK_EXCEPTION, error));
                }
            }
        } else {
            processChain.doProcess(req, res, processChain);
        }
    }
}

