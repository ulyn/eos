/**
 * @(#)ExceptionHandler
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2015
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 15-1-19 下午4:00
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.exception;

import com.sunsharing.eos.common.filter.ServiceRequest;
import com.sunsharing.eos.common.filter.ServiceResponse;

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
public class ExceptionHandler {

    public static void tryHandleException(
            ServiceRequest request, ServiceResponse response, ExceptionResolver exceptionResolver) {
        if (response.hasException() && exceptionResolver != null) {
            Throwable ex = response.getException();
            exceptionResolver.resolveException(request, response, ex);
        }
    }

}

