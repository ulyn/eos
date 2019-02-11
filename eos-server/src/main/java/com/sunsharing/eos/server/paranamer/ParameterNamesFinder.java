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

public class ParameterNamesFinder {

    public final static String GENERATED_HOLDER_CLASSNAME = "AnnotationProcessorHolderImpl";

    private ParameterNamesHolder holder = null;

    public ParameterNamesHolder find() {
        if (holder == null) {
            synchronized (this) {
                if (holder == null) {
                    try {
                        String generatedClass = ParameterNamesFinder.class.getPackage().getName() + "." + GENERATED_HOLDER_CLASSNAME;
                        Class cls = Class.forName(generatedClass);
                        holder = new EnhanceHolderImpl((ParameterNamesHolder) cls.newInstance());
                    } catch (Exception e) {
                        holder = new EnhanceHolderImpl(null);
                    }
                }
            }
        }
        return holder;
    }

}
