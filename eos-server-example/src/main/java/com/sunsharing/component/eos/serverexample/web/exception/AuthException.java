package com.sunsharing.component.eos.serverexample.web.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yl
 * Date: 12-12-26
 * Time: 上午11:16
 * 请求未登录时错误抛出的异常
 */
public class AuthException extends Exception {

    public AuthException(Throwable root) {
        super(root);
    }

    public AuthException(String message, Throwable root) {
        super(message, root);
    }

    public AuthException(String message) {
        super(message);
    }
}
