package com.mtl.springFramework.core;

import com.mtl.springFramework.annotation.MtlController;
import com.mtl.springFramework.annotation.MtlRquestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * 说明:前端控制器
 *
 * @作者 莫天龙
 * @时间 2019/10/24 15:11
 */
public class MyDispatcherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //获取请求路径
        String requestURI = req.getRequestURI();
        MyApplicationContextImpl applicationContext = MyApplicationContextImpl.getInstance();
        MvcInvocation mvcInvocation = applicationContext.getMvcInvocation(requestURI);
        if (mvcInvocation==null){
            resp.getWriter().println("404!");
            return ;
        };
        //执行路径对应的方法
        Object target = mvcInvocation.getTarget();
        Method method = mvcInvocation.getMethod();
        try {
            Object ret = method.invoke(target, req, resp);
            resp.getWriter().println(ret);
        } catch (Exception e) {
            Throwable t=e;
            if (e instanceof InvocationTargetException){
                t=((InvocationTargetException) e).getTargetException();
            }
            t.printStackTrace();
            resp.getWriter().println("500!\n"+t.getMessage()+" \n"+ Arrays.toString(t.getStackTrace()));
        }
    }

    @Override
    public void init() throws ServletException {
        //请求对应关系建立
        createRequestMapping();
        MyApplicationContextImpl instance = MyApplicationContextImpl.getInstance();
        System.out.println("init mvc successful!");
    }

    private void createRequestMapping() {
        MyApplicationContextImpl instance = MyApplicationContextImpl.getInstance();
        Map<String, Object> ioc = instance.getIoc();
        if (ioc==null) return;
        Iterator<Map.Entry<String, Object>> iterator = ioc.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            Class<?> clazz = bean.getClass();
            if (clazz.isAnnotationPresent(MtlController.class)){
                String basePath="/";
                if (clazz.isAnnotationPresent(MtlRquestMapping.class)) basePath=basePath+clazz.getAnnotation(MtlRquestMapping.class).value();
                Method[] declaredMethods = clazz.getDeclaredMethods();
                for (Method declaredMethod : declaredMethods) {
                    if (!declaredMethod.isAnnotationPresent(MtlRquestMapping.class)) continue;
                    String path = declaredMethod.getAnnotation(MtlRquestMapping.class).value();
                    if ("".equals(path.trim())) throw new RuntimeException("RquestMapping caan not be null!");
                    path=basePath+"/"+path;
                    MvcInvocation mvcInvocation=new MvcInvocation(bean, declaredMethod, clazz.getName());
                    mvcInvocation.setBeanName(beanName);
                    instance.addRequstMapping(path.replaceAll("//+", "/"), mvcInvocation);
                }
            }
        }
    }
}
