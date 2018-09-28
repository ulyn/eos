/*
 * @(#) ValidSupportAspect
 * 版权声明 厦门畅享信息技术有限公司, 版权所有 违者必究
 *
 * <br> Copyright:  Copyright (c) 2018
 * <br> Company:厦门畅享信息技术有限公司
 * <br> @author ulyn
 * <br> 2018-09-28 12:57:08
 */

package com.sunsharing.eos.support;

import com.sunsharing.eos.common.annotation.Version;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.annotation.Resource;
import javax.validation.Valid;

@Aspect
@Component
public class ValidSupportAspect {

    @Autowired
    private Validator validator;
    @Resource(name = "shareExceptionHandlerAdvice4Eos")
    protected ShareExceptionHandlerAdvice4Eos shareExceptionHandlerAdvice4Eos;

    @Before("execution(* *(@org.springframework.validation.annotation.Validated (*)))")
    public void validated(JoinPoint jp) throws MethodArgumentNotValidException {
        doValid(jp);
    }

    @Before("execution(* *(@javax.validation.Valid (*)))")
    public void valid(JoinPoint jp) throws MethodArgumentNotValidException {
        doValid(jp);
    }

    public void doValid(JoinPoint jp) throws MethodArgumentNotValidException {
        if (ArrayUtils.isNotEmpty(jp.getArgs())) {

            // Get the target method
            Method interfaceMethod = ((MethodSignature) jp.getSignature()).getMethod();
            // Method implementationMethod = jp.getTarget().getClass().getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());
            if (interfaceMethod.getAnnotation(Version.class) == null) {
                //非EOS接口的，不做校验动作。
                return;
            }
            // Get the annotated parameters and validate those with the @Valid annotation
            Annotation[][] annotationParameters = interfaceMethod.getParameterAnnotations();
            for (int i = 0; i < annotationParameters.length; i++) {
                Annotation[] annotations = annotationParameters[i];
                for (Annotation paramAnn : annotations) {
                    boolean valid = false;
                    Object[] validationHints = null;
                    if (paramAnn.annotationType().equals(Valid.class)) {
                        valid = true;
                    } else if (paramAnn.annotationType().equals(Validated.class)) {
                        valid = true;
                        Validated validatedAnn = (Validated) paramAnn;
                        Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(paramAnn));
                        validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
                    }
                    if (valid) {
                        Object arg = jp.getArgs()[i];
                        checkNotNull(arg);
                        BindingResult bindingResult =
                            new BeanPropertyBindingResult(arg, arg.getClass().getSimpleName());
                        if (!ObjectUtils.isEmpty(validationHints) && validator instanceof SmartValidator) {
                            ((SmartValidator) validator).validate(arg, bindingResult, validationHints);
                        } else if (validator != null) {
                            validator.validate(arg, bindingResult);
                        }
                        if (bindingResult.hasErrors()) {
                            throw shareExceptionHandlerAdvice4Eos.trans2ValidShareException(
                                new MethodArgumentNotValidException(new MethodParameter(interfaceMethod, i), bindingResult)
                            );
                        }
                    }
                }
            }


        }
    }

    private void checkNotNull(Object o) {
        if (o == null) {
            throw new RuntimeException("入参为null，无法校验");
        }
    }


}