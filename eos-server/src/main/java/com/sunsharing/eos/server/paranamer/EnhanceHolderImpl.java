/*
 * @(#) ParanamerHolderImpl
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2019
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2019-02-11 14:30:05
 */

package com.sunsharing.eos.server.paranamer;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

import java.lang.reflect.Method;

/**
 * 兼容旧版本服务使用Paranamer的holder
 */
public class EnhanceHolderImpl implements ParameterNamesHolder {

    private ParameterNamesHolder holder;

    public EnhanceHolderImpl(ParameterNamesHolder holder) {
        this.holder = holder;
    }

    private Paranamer adaptiveParanamer = new AdaptiveParanamer();

    @Override
    public String[] getParameterNames(Class interfaces, Method method) {
        try {
            return adaptiveParanamer.lookupParameterNames(method, true);
        } catch (ParameterNamesNotFoundException e) {
            if (holder == null) {
                throw new RuntimeException("该服务接口未能正常编译方法参数：" + interfaces.getName());
            }
            return holder.getParameterNames(interfaces, method);
        }
    }
}
