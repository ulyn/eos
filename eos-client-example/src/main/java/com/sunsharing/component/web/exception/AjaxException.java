package com.sunsharing.component.web.exception;

/**
 * Created with IntelliJ IDEA.
 * User: yl
 * Date: 12-12-26
 * Time: 上午11:16
 * Ajax请求时错误抛出的异常
 */
public class AjaxException extends Exception {

    public AjaxException(Throwable root) {
        super(root);
    }

    public AjaxException(String message, Throwable root) {
        super(message, root);
    }

    public AjaxException(String message) {
        super(message);
    }
}
