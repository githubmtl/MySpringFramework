package com.mtl.springFramework.core;

import java.util.List;

/**
 * 说明:保存一个方法需要增强哪方面
 *
 * @作者 莫天龙
 * @时间 2019/10/24 22:58
 */
public class AopDefineMethodInfo {
    private String methodName;
    private List<AopInvocation> advices;

    public AopDefineMethodInfo() {
    }

    public AopDefineMethodInfo(String methodName, List<AopInvocation> advices) {
        this.methodName = methodName;
        this.advices = advices;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<AopInvocation> getAdvices() {
        return advices;
    }

    public void setAdvices(List<AopInvocation> advices) {
        this.advices = advices;
    }
}
