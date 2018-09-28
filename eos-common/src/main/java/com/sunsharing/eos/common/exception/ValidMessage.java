/*
 * @(#) ValidMessage
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2018
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2018-09-28 17:05:26
 */

package com.sunsharing.eos.common.exception;

import java.io.Serializable;

public class ValidMessage implements Serializable{

    private String field;
    private String code;
    private String message;

    public ValidMessage(String field, String code, String message) {
        this.field = field;
        this.code = code;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
