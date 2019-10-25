package com.mtl.springFramework.core;

import com.mtl.springFramework.annotation.MtlAspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 说明:封装AOP执行类
 *
 * @作者 莫天龙
 * @时间 2019/10/24 19:00
 */
public class AopInvocation extends MvcInvocation {
    private Class<?> annotationType;

    private Object[] args;
    public AopInvocation() {
    }

    public AopInvocation(Object target, Method method, String className, Class<?> annotationType) {
        super(target, method, className);
        this.annotationType = annotationType;
    }

    public Class<?> getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(Class<?> annotationType) {
        this.annotationType = annotationType;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
