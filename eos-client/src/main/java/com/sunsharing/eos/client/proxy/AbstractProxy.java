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

import com.sunsharing.eos.common.rpc.ClientProxy;
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
     * @throws Exception
     */
//    public Object doInvoke(RpcInvocation invocation, ServiceConfig config) throws Exception {
//        if ("toString".equals(invocation.getMethodName())) {
//            //打印getBean获得的对象时，直接返回服务名称
//            return String.format("eos service [%s-%s-%s]", config.getAppId(), config.getId(), config.getVersion());
//        }
//        return new StubRpc().doInvoke(invocation, config);
//    }

}

