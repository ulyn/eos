/*
 * @(#) ParameterNamesNotFoundException
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2019
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2019-02-11 16:02:50
 */

package com.sunsharing.eos.server.paranamer;

public class ParameterNamesNotFoundException extends RuntimeException {

    private Exception cause;

    public ParameterNamesNotFoundException(String message, Exception cause) {
        super(message);
        this.cause = cause;
    }

    public ParameterNamesNotFoundException(String message) {
        super(message);
    }

    public Throwable getCause() {
        return cause;
    }

}
