package com.mtl.springFramework.core;

import com.mtl.springFramework.annotation.MtlAfter;
import com.mtl.springFramework.annotation.MtlAfterReturnning;
import com.mtl.springFramework.annotation.MtlAfterthrowing;
import com.mtl.springFramework.annotation.MtlBefore;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 说明:AOP创建代理对象
 *
 * @作者 莫天龙
 * @时间 2019/10/24 21:17
 */
public class ProxyCreator {
    public static Object create(AopProxyDefine aopProxyDefine){
        Class<?> clazz = aopProxyDefine.getTarget().getClass();
        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces==null||interfaces.length==0){//CGLIB 方式创建代理对象
            Enhancer enhancer=new Enhancer();
            enhancer.setCallback(new CglibMethodInterceptor(aopProxyDefine));
            enhancer.setSuperclass(clazz);
            return enhancer.create();
        }else{//JDK 动态代理
            Object o = Proxy.newProxyInstance(clazz.getClassLoader(), interfaces, new JdkMethodInterceptor(aopProxyDefine));
            return o;
        }
    }

    /**
     * CGLib proxy handler
     */
    private static class CglibMethodInterceptor implements MethodInterceptor{
        private AopProxyDefine aopProxyDefine;

        public CglibMethodInterceptor(AopProxyDefine aopProxyDefine) {
            this.aopProxyDefine = aopProxyDefine;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
            return invokMethod(o, method, objects, aopProxyDefine, methodProxy, false);
        }
    }

    /**
     * JDK proxy handler
     */
    private static class JdkMethodInterceptor implements InvocationHandler{
        private AopProxyDefine aopProxyDefine;

        public JdkMethodInterceptor(AopProxyDefine aopProxyDefine) {
            this.aopProxyDefine=aopProxyDefine;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return invokMethod(aopProxyDefine.getTarget(), method, args, aopProxyDefine, aopProxyDefine.getTarget(), true);
        }
    }

    /**
     * 查看某方法是否需要添加某个通知
     * @param advices
     * @param clazz
     * @return
     */
    private static AopInvocation hasAnnotation(List<AopInvocation> advices,Class<?> clazz){
        for (AopInvocation advice : advices) {
            if (advice.getAnnotationType()==clazz) return advice;
        }
        return null;
    }

    /**
     * 方法执行
     * @param o 被代理对象
     * @param method    被执行的方法
     * @param objects   方法参数
     * @param aopProxyDefine    方法增强信息
     * @param invoker           执行器，CGlib代理为MethodProxy，JDK 动态代理为被代理对象
     * @param isJdkProxy        是否为JDK动态代理
     * @return 方法返回值
     */
    private static Object invokMethod(Object o, Method method,Object[] objects,AopProxyDefine aopProxyDefine,Object invoker,boolean isJdkProxy) throws Throwable {
        String methodName = method.getName();
        AopDefineMethodInfo interceptorMethodInfo = aopProxyDefine.getInterceptorMethodInfo(methodName);
        if (interceptorMethodInfo==null) return invokTargetMethod(o, method, invoker, objects, isJdkProxy);//不需要拦截
        List<AopInvocation> advices = interceptorMethodInfo.getAdvices();
        if (advices==null||advices.size()==0) return invokTargetMethod(o, method, invoker, objects, isJdkProxy);//不需要拦截
        try {
            AopInvocation beforeAnnotation = hasAnnotation(advices, MtlBefore.class);
            if (beforeAnnotation!=null){//执行前置通知
                beforeAnnotation.getMethod().invoke(beforeAnnotation.getTarget());
            }
            //执行原方法
            Object reObj = invokTargetMethod(o, method, invoker, objects, isJdkProxy);
            AopInvocation afterReturnningAnn = hasAnnotation(advices, MtlAfterReturnning.class);
            if (afterReturnningAnn != null) {//返回通知
                afterReturnningAnn.getMethod().invoke(afterReturnningAnn.getTarget(),reObj);
            }
            return reObj;
        }catch (Throwable t){
            if (t instanceof InvocationTargetException) t=((InvocationTargetException) t).getTargetException();
            AopInvocation afterThrowingAnn = hasAnnotation(advices, MtlAfterthrowing.class);
            if (afterThrowingAnn!=null){//异常通知
                afterThrowingAnn.getMethod().invoke(afterThrowingAnn.getTarget(), t);
            }
            throw t;
        }finally {
            AopInvocation afterAopAnnotation = hasAnnotation(advices, MtlAfter.class);
            if (afterAopAnnotation!=null){//最终通知
                afterAopAnnotation.getMethod().invoke(afterAopAnnotation.getTarget());
            }
        }
    }

    /**
     * 执行被代理方法
     * @param target
     * @param method
     * @param invoker
     * @param args
     * @param isJdkProxy
     * @return
     * @throws Throwable
     */
    private static Object invokTargetMethod(Object target,Method method,Object invoker,Object[] args,boolean isJdkProxy) throws Throwable {
        if (!isJdkProxy){
            MethodProxy methodProxy = (MethodProxy) invoker;
            return methodProxy.invokeSuper(target, args);
        }
        return method.invoke(target,args);
    }
}
