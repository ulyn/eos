package com.sunsharing.eos.clientexample.web.exception;

import com.sunsharing.eos.clientexample.web.common.BaseController;
import com.sunsharing.eos.clientexample.web.common.ResponseHelper;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExceptionResolver extends BaseController implements HandlerExceptionResolver {
    Logger log = Logger.getLogger(ExceptionResolver.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object object, Exception ex) {
        log.error("Handle exception:" + ex.getClass().getName(), ex);
        String accept = request.getHeader("Accept");
        accept = accept == null ? "" : accept.toLowerCase();
        if (ex instanceof AjaxException || accept.indexOf("json") != -1 || accept.equals("*/*")) {
            String errorMsg = ex.getMessage();
            if (errorMsg == null) {
                errorMsg = "内部处理异常！";
            }
            ResponseHelper.printOut(response, false, errorMsg, "");
            return null;
        } else if (ex instanceof AuthException) {
            // 未登录
            StringBuilder builder = new StringBuilder();
            builder.append("<script type=\"text/javascript\" charset=\"UTF-8\">");
            builder.append("alert('您还没有登陆或者页面过期，请重新登录');");
            builder.append("top.location.href='");
            String path = request.getContextPath();
            String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
            builder.append(basePath);
            builder.append("login.do';</script>");
            ResponseHelper.printOut(response, builder.toString());
            return null;
        } else if (ex instanceof org.springframework.web.multipart.MaxUploadSizeExceededException) {
            //此处属于文件大小超过配置的文件大小限制
            ResponseHelper.printOut(response, false, "文件超过系统预定义大小，请重新选择！", "");
            return null;
        } else {
            ModelAndView model = new ModelAndView("errors/500");
            model.addObject("status", false);
            model.addObject("message", ex.getMessage());
            return model;
        }
    }
}
