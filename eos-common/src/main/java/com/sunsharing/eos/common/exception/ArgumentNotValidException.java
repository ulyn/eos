/*
 * @(#) ValidShareException
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2018
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2018-09-28 17:05:50
 */

package com.sunsharing.eos.common.exception;


import java.util.List;

//参数校验的异常
public class ArgumentNotValidException extends RuntimeException{

    private String status;
    private String message;
    private List<ValidMessage> errorDetail;

    public ArgumentNotValidException() {
    }

    public ArgumentNotValidException(String status, String message, List<ValidMessage> errorDetail) {
        this.status = status;
        this.message = message;
        this.errorDetail = errorDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ValidMessage> getErrorDetail() {
        return errorDetail;
    }

    public void setErrorDetail(List<ValidMessage> errorDetail) {
        this.errorDetail = errorDetail;
    }
}
