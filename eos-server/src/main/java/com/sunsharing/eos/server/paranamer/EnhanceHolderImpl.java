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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 兼容旧版本服务使用Paranamer的holder
 */
public class EnhanceHolderImpl implements ParameterNamesHolder {

    private Map<Class, ParameterNamesHolder> holderMap = new HashMap<Class, ParameterNamesHolder>();

    public EnhanceHolderImpl(List<ParameterNamesHolder> holders) {
        for (ParameterNamesHolder holder : holders) {
            holderMap.put(holder.getInterfaceClass(), holder);
        }
    }

    private Paranamer adaptiveParanamer = new AdaptiveParanamer();

    @Override
    public Class getInterfaceClass() {
        return this.getClass();
    }

    @Override
    public String[] getParameterNames(Class interfaces, Method method) {
        if (holderMap.containsKey(interfaces)) {
            try {
                ParameterNamesHolder holder = holderMap.get(interfaces);
                String[] parameterNames = holder.getParameterNames(interfaces, method);
                return parameterNames;
            } catch (com.sunsharing.eos.server.paranamer.ParameterNamesNotFoundException e) {
                //ignor...
            }
        }
        return adaptiveParanamer.lookupParameterNames(method, false);
    }
}
