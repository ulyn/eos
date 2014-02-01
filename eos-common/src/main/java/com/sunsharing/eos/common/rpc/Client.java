/**
 * @(#)Client
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-31 下午11:43
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.rpc;

import com.sunsharing.eos.common.rpc.impl.RpcResult;
import com.sunsharing.eos.common.serialize.Serialization;

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
public interface Client {
    /**
     * 执行远程调用的方法
     *
     * @param invocation
     * @param serializationType
     * @param url
     * @param port
     * @return
     */
    RpcResult doRpc(Invocation invocation, String serializationType, String url, int port) throws Throwable;
}

