/**
 * @(#)CallWsException
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2013
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 13-8-5 上午10:17
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.clientproxy.ws;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> 调用ws异常
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class CallWsException extends Exception {

    public CallWsException() {
    }

    public CallWsException(String message) {
        super(message);
    }

    public CallWsException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallWsException(Throwable cause) {
        super(cause);
    }
}

