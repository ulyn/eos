/**
 * @(#)RpcServer
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-22 下午3:07
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
import com.sunsharing.eos.common.config.ServiceConfig;

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
public interface RpcServer {
    int getPort();

    void register(Object impl, ServiceConfig config);

    boolean isRunning();

    void stop();

    void start();

    ServiceResponse callService(ServiceRequest request);
}

