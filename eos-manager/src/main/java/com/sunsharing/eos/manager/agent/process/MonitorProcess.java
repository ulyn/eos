/**
 * @(#)MonitorProcess
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-6 下午11:51
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.process;

import com.sunsharing.eos.common.Constants;
import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;
import com.sunsharing.eos.manager.zookeeper.EosMonitor;
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
public class MonitorProcess implements Process {

    Logger logger = Logger.getLogger(MonitorProcess.class);

    @Override
    public void doProcess(RequestPro req, ResponsePro res, ProcessChain processChain) {
        //todo 监控处理
        if (res.getStatus() == Constants.STATUS_ERROR) {
            //调用出错了。。。
        }
        EosMonitor.getInstance().addServiceCallCount(req.getAppId(), req.getServiceId(), req.getServiceVersion());
        processChain.doProcess(req, res, processChain);
    }
}

