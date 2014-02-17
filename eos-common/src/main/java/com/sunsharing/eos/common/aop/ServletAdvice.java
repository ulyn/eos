/**
 * @(#)ServletAdvice
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-17 下午9:53
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.aop;

import com.sunsharing.eos.common.config.ServiceMethod;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br>  服务方法切面接口
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
public interface ServletAdvice {

    /**
     * 方法调用前执行
     *
     * @param request
     * @param method
     * @param args
     * @return
     */
    AdviceResult before(HttpServletRequest request, ServiceMethod method, Object[] args);

    /**
     * 方法调用后执行
     *
     * @param request
     * @param method
     * @param args
     * @param returnVal
     * @return
     */
    AdviceResult after(HttpServletRequest request, ServiceMethod method, Object[] args, Object returnVal);

}