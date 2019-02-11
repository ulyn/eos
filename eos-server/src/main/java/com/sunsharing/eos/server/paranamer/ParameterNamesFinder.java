/*
 * @(#) ParameterNamesFinder
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2019
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2019-02-10 11:40:59
 */

package com.sunsharing.eos.server.paranamer;

import com.sunsharing.eos.common.utils.ClassFilter;
import com.sunsharing.eos.common.utils.ClassUtils;

import java.util.ArrayList;
import java.util.List;

public class ParameterNamesFinder {

    private ParameterNamesHolder holder = null;

    public ParameterNamesHolder find() {
        if (holder == null) {
            synchronized (this) {
                if (holder == null) {
                    try {
                        List<Class> classes = ClassUtils.scanPackage(ParameterNamesFinder.class.getPackage().getName(), new ClassFilter() {
                            @Override
                            public boolean accept(Class clazz) {
                                return ParameterNamesHolder.class.isAssignableFrom(clazz)
                                    && !clazz.isInterface()
                                    && clazz != EnhanceHolderImpl.class;
                            }
                        });
                        List<ParameterNamesHolder> parameterNamesHolders = new ArrayList<ParameterNamesHolder>();
                        for (Class cls : classes) {
                            parameterNamesHolders.add((ParameterNamesHolder) cls.newInstance());
                        }
                        holder = new EnhanceHolderImpl(parameterNamesHolders);
                    } catch (Exception e) {
                        holder = new EnhanceHolderImpl(new ArrayList<ParameterNamesHolder>());
                    }
                }
            }
        }
        return holder;
    }

}
