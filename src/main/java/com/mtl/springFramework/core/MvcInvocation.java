package com.mtl.springFramework.core;

import java.lang.reflect.Method;

/**
 * 说明:封装前置执行器
 *
 * @作者 莫天龙
 * @时间 2019/10/24 15:18
 */
public class MvcInvocation {
    private Object target;
    private Method method;
    private String className;
    private String beanName;
    private String pointCut;

    public MvcInvocation() {
    }

    public MvcInvocation(Object target, Method method, String className) {
        this.target = target;
        this.method = method;
        this.className = className;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getPointCut() {
        return pointCut;
    }

    public void setPointCut(String pointCut) {
        this.pointCut = pointCut;
    }
}
