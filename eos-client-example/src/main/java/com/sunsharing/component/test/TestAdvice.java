/**
 * @(#)TestAdvice
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-18 上午10:17
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.component.test;

import com.sunsharing.eos.common.aop.AdviceResult;
import com.sunsharing.eos.common.aop.ServletAdvice;
import com.sunsharing.eos.common.config.ServiceMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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
public class TestAdvice implements ServletAdvice {
    @Override
    public AdviceResult before(HttpServletRequest request, ServiceMethod method, Object[] args) {
        System.out.println(method.getMethodName() + "被执行前，入参为" + Arrays.toString(args));
        return new AdviceResult(false, null);
    }

    @Override
    public AdviceResult after(HttpServletRequest request, ServiceMethod method, Object[] args, Object returnVal) {
        System.out.println(method.getMethodName() + "被执行后，returnVal=" + returnVal);
        return new AdviceResult(true, returnVal);
    }
}

