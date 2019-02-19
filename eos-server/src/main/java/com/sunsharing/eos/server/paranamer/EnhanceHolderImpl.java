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
import com.thoughtworks.paranamer.Paranamer;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 兼容旧版本服务使用Paranamer的holder
 */
public class EnhanceHolderImpl implements ParameterNamesHolder {

    private List<ParameterNamesHolder> holders;

    public EnhanceHolderImpl(List<ParameterNamesHolder> holders) {
        this.holders = holders;
    }

    private Paranamer adaptiveParanamer = new AdaptiveParanamer();

    @Override
    public String[] getParameterNames(Class interfaces, Method method) {
        for (ParameterNamesHolder holder : holders) {
            try {
                String[] parameterNames = holder.getParameterNames(interfaces, method);
                return parameterNames;
            } catch (com.sunsharing.eos.server.paranamer.ParameterNamesNotFoundException e) {
                //ignor...
                continue;
            }
        }
        return adaptiveParanamer.lookupParameterNames(method, false);
    }
}
