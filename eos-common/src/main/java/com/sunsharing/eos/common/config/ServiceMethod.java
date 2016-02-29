/**
 * @(#)ServiceMethod
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 *<br> Copyright:  Copyright (c) 2014
 *<br> Company:厦门畅享信息技术有限公司
 *<br> @author ulyn
 *<br> 14-2-10 下午2:13
 *<br> @version 1.0
 *————————————————————————————————
 *修改记录
 *    修改者：
 *    修改时间：
 *    修改原因：
 *————————————————————————————————
 */
package com.sunsharing.eos.common.config;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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
public class ServiceMethod {

    public static enum AccessType {
        PUBLIC, PROTECTED, PRIVATE;

        public static AccessType valueOf(int modifiers) {
            if (Modifier.isPublic(modifiers)) {
                return PUBLIC;
            } else if (Modifier.isPrivate(modifiers)) {
                return PRIVATE;
            } else if (Modifier.isProtected(modifiers)) {
                return PROTECTED;
            } else return null;
        }
    }

    private String methodName;
    private String version;
    private Class<?>[] parameterTypes;
    private String[] parameterNames;
    private Class retType;
    private AccessType accessType;

    public ServiceMethod() {
    }

    public ServiceMethod(Method method) {
        new ServiceMethod(method, null);
    }

    public ServiceMethod(Method method, String[] parameterNames) {
        this.methodName = method.getName();
        this.accessType = AccessType.valueOf(method.getModifiers());
        this.retType = method.getReturnType();
        this.parameterTypes = method.getParameterTypes();
        this.parameterNames = parameterNames;
        this.version = version;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class getRetType() {
        return retType;
    }

    public void setRetType(Class retType) {
        this.retType = retType;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取方法的参数名，此方法只有当注解扫描到注解时候有值，为了前端js调用组织入参用
     *
     * @return
     */
    public String[] getParameterNames() {
        return parameterNames;
    }

    public void setParameterNames(String[] parameterNames) {
        this.parameterNames = parameterNames;
    }

}

