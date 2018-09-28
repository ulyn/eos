/*
 * @(#) ShareExceptionHandlerAdvice
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2018
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2018-09-28 18:38:55
 */

package com.sunsharing.eos.support;

import com.sunsharing.eos.common.exception.ArgumentNotValidException;
import com.sunsharing.eos.common.rpc.RpcException;
import com.sunsharing.share.webex.advice.AbstractShareExceptionHandlerAdvice;
import com.sunsharing.share.webex.entity.ShareResponse;
import com.sunsharing.share.webex.entity.ValidMessage;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

@Component
public class ShareExceptionHandlerAdvice4Eos extends AbstractShareExceptionHandlerAdvice {

    @Override
    public ShareResponse handlerAllException(HttpServletRequest req, Exception ex) {
        Throwable cause = ex;
        if (cause instanceof RpcException) {
            cause = ex.getCause();
        } else if (cause instanceof UndeclaredThrowableException) {
            cause = ((UndeclaredThrowableException) cause).getUndeclaredThrowable();
        }
        if (cause instanceof Exception) {
            ex = (Exception) cause;
        } else {
            ex = new Exception(cause.getMessage(), cause);
        }
        return super.handlerAllException(req, ex);
    }

    @Override
    protected Optional<ShareResponse> tryHandleException(HttpServletRequest req, Exception ex) {
        if (ex instanceof ArgumentNotValidException) {
            ArgumentNotValidException argumentNotValidException = (ArgumentNotValidException) ex;
            return Optional.of(ShareResponse.fail(argumentNotValidException.getStatus(),
                argumentNotValidException.getMessage(),
                argumentNotValidException.getErrorDetail()));
        }
        return super.tryHandleException(req, ex);
    }

    public ArgumentNotValidException trans2ValidShareException(MethodArgumentNotValidException ex) {
        ShareResponse shareResponse = handlerMethodArgumentNotValidException(ex, null);
        List<ValidMessage> errorDetail = (List<ValidMessage>) shareResponse.getErrorDetail();
        List<com.sunsharing.eos.common.exception.ValidMessage> errors = null;
        if (errorDetail != null) {
            errors = new ArrayList<com.sunsharing.eos.common.exception.ValidMessage>(errorDetail.size());
            for (ValidMessage item : errorDetail) {
                errors.add(new com.sunsharing.eos.common.exception.ValidMessage(item.getField(), item.getCode(), item.getMessage()));
            }
        }
        return new ArgumentNotValidException(
            shareResponse.getStatus(),
            shareResponse.getMessage(),
            errors);
    }
}
