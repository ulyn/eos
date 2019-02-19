/*
 * @(#) ServiceMethodParamter
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2019
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2019-02-10 11:35:16
 */

package com.sunsharing.eos.server.paranamer;

import java.lang.reflect.Method;

/**
 * 接口的参数名获取
 */
public interface ParameterNamesHolder {

    String[] getParameterNames(Class interfaces, Method method);

}
