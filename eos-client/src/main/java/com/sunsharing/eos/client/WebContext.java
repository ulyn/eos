/**
 * @(#)RpcServletContext
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-19 上午10:22
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.client;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>  Web请求的上下文
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public class WebContext {

    private static class LocalContainer {
        private HttpServletRequest request;
        private HttpServletResponse response;

        private LocalContainer() {
        }

        private LocalContainer(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public HttpServletResponse getResponse() {
            return response;
        }

        public void setResponse(HttpServletResponse response) {
            this.response = response;
        }
    }

    private static ThreadLocal<LocalContainer> local = new ThreadLocal<LocalContainer>(){
        @Override
        protected LocalContainer initialValue() {
            return new LocalContainer();
        }
    };

    public static HttpServletRequest getRequest() {
        return local.get().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return local.get().getResponse();
    }

    public static void setContext(HttpServletRequest request, HttpServletResponse response) {
        local.set(new LocalContainer(request,response));
    }

    public static void remove() {
        local.remove();
    }
}

