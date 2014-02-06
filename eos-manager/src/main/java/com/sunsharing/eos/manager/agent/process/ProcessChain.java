/**
 * @(#)ProcessChain
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-5 下午3:11
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.manager.agent.process;

import com.sunsharing.eos.common.rpc.protocol.RequestPro;
import com.sunsharing.eos.common.rpc.protocol.ResponsePro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class ProcessChain implements Process {

    private List<Process> processList = new ArrayList<Process>();

    public void setProcessList(Process[] processes) {
        this.processList = Arrays.asList(processes);
    }

    public ProcessChain addProcess(Process process) {
        processList.add(process);
        return this;
    }

    int index = 0;

    @Override
    public void doProcess(RequestPro req, ResponsePro res, ProcessChain processChain) {
        if (index == processList.size()) return;
        Process f = processList.get(index);
        index++;
        //依次执行下一个过滤器，直到整个过滤器链执行完
        f.doProcess(req, res, processChain);
    }
}

