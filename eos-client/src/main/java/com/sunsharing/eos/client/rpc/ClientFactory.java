/**
 * @(#)ClientFactory
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-1 上午12:20
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client.rpc;

import com.sunsharing.eos.common.rpc.Client;

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
public class ClientFactory {

    public static Client getClient(String transporter) {
        if ("netty".equals(transporter)) {
            return new SocketClient();
        } else if ("socket".equals(transporter)) {
            return new SocketClient();
        } else {
            throw new RuntimeException("没有该transporter的实现client:" + transporter);
        }
    }
}

