/**
 * @(#)EosService
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-1-30 下午2:50
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.annotation;

import com.sunsharing.eos.common.Constants;

import java.lang.annotation.*;

/**
 * <pre></pre>
 * <br>----------------------------------------------------------------------
 * <br> <b>功能描述:</b>
 * <br> Eos服务的注解
 * <br> 注意事项:
 * <br>
 * <br>
 * <br>----------------------------------------------------------------------
 * <br>
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EosService {

    String id() default "";

    String version();

    String proxy() default Constants.DEFAULT_PROXY;

    String serialization() default Constants.DEFAULT_SERIALIZATION;

    String transporter() default Constants.DEFAULT_TRANSPORTER;

    String appId() default "";

    String impl() default "";

    int timeout() default 30000;

}

