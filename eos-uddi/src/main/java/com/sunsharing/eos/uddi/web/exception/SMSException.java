/**
 * @(#)$CurrentFile
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2013
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author criss
 *<br> 13-11-13 下午2:34
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.uddi.web.exception;

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
public class SMSException extends RuntimeException {
    //没有注册
    public static final String NO_REGEIST = "01";
    //校验码没通过
    public static final String SIG_NOPASS = "02";

    String code;
    String msg;
    public SMSException(String code,String msg)
    {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}

