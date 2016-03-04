/**
 * @(#)RpcClient
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

import com.sunsharing.eos.common.ServiceRequest;
import com.sunsharing.eos.common.ServiceResponse;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;

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
public interface RpcClient {
    /**
     * 执行远程调用的方法
     *
     * @param request
     * @param ip
     * @param port
     * @return
     */
    ServiceResponse doRpc(ServiceRequest request, String ip, int port) throws Throwable;

    /**
     * 执行远程调用的方法
     *
     * @param request
     * @param ip
     * @param port
     * @return
     */
    ResponsePro doRpc(RequestPro request, String ip, int port) throws Throwable;
}

