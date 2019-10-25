package com.mtl.springFramework.core;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明:保存一个类需要增强的方法及怎么增强
 *
 * @作者 莫天龙
 * @时间 2019/10/24 22:56
 */
public class AopProxyDefine {
    private Object target;
    private List<AopDefineMethodInfo> methodInfos=new ArrayList<>();


    public AopProxyDefine(Object target) {
        this.target = target;
    }

    void addAopMethod(AopDefineMethodInfo aopDefineMethodInfo){
        methodInfos.add(aopDefineMethodInfo);
    }
    Object getTarget(){
        return target;
    }


    /**
     * 获取需要拦截的方法的advices
     * @param methodName
     * @return
     */
    AopDefineMethodInfo getInterceptorMethodInfo(String methodName){
        for (AopDefineMethodInfo methodInfo : methodInfos) {
            if (methodName.equals(methodInfo.getMethodName())){
                return methodInfo;
            }
        }
        return null;
    }

    /**
     * 获取需要增强的方法数量
     * @return
     */
    int getAdviceMethodSize(){
        return methodInfos.size();
    }
}
