package com.mtl.springFramework.core;

import com.mtl.springFramework.annotation.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * 说明：初始化IOC容器
 *
 * @作者 莫天龙
 * @时间 2019/10/24 9:18
 */
public class MyContextLoaderListener implements ServletContextListener {
    private List<String> clazzs = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //获取配置文件
        loadConfig(servletContextEvent.getServletContext().getInitParameter("configLocation"));
        //扫描指定路径下的类文件
        try {
            scanClass();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("applicationContext init failed!", e);
        }
        //初始化符合规则的类
        try {
            newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("class instance failed !", e);
        }
        //创建AOP
        doAop();
        //执行依赖注入
        try {
            di();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("DI dependency injection failed!", e);
        }
        System.out.println("init myIoc successful！");
       // MyApplicationContextImpl applicationContext = MyApplicationContextImpl.getInstance();
       // System.out.println(applicationContext);
    }

    private void doAop() {
        //为AOP创建代理对象
        MyApplicationContextImpl applicationContext = MyApplicationContextImpl.getInstance();
        Map<String, Object> ioc = applicationContext.getIoc();
        if (ioc.size()==0) return;
        Iterator<Map.Entry<String, Object>> iterator = ioc.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            String beanName = entry.getKey();
            Object bean = entry.getValue();
            String className = bean.getClass().getName();
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            if (declaredMethods==null||declaredMethods.length==0) continue;
            AopProxyDefine aopProxyDefine=new AopProxyDefine(bean);
            for (Method declaredMethod : declaredMethods) {
                String methodName = declaredMethod.getName();
                List<AopInvocation> aopInvocations = getAopInvocation(className + "." + methodName);
                if (aopInvocations!=null&&aopInvocations.size()>0){//需要拦截的方法大于0
                    AopDefineMethodInfo aopDefineMethodInfo=new AopDefineMethodInfo(methodName,aopInvocations);
                    aopProxyDefine.addAopMethod(aopDefineMethodInfo);
                }
            }
            if (aopProxyDefine.getAdviceMethodSize()>0){//需要增强，才创建代理对象
                Object proxyBean=ProxyCreator.create(aopProxyDefine);
                applicationContext.setBean(beanName, proxyBean);
            }
        }
    }

    /**
     * 根据方法全名获取通知
     * @param s
     * @return
     */
    private List<AopInvocation> getAopInvocation(String s) {
        List<AopInvocation> list=new ArrayList<>();
        List<AopInvocation> aopMappings = MyApplicationContextImpl.getInstance().getAopMappings();
        if (aopMappings.size()>0){
            for (AopInvocation aopMapping : aopMappings) {
                if (s.equals(aopMapping.getPointCut())) list.add(aopMapping);
            }
        }
        return list;
    }

    private Class isAdvice(Method declaredMethod) {
        if (declaredMethod.isAnnotationPresent(MtlBefore.class)) return  MtlBefore.class;
        if (declaredMethod.isAnnotationPresent(MtlAfter.class)) return  MtlAfter.class;
        if (declaredMethod.isAnnotationPresent(MtlAfterReturnning.class)) return  MtlAfterReturnning.class;
        if (declaredMethod.isAnnotationPresent(MtlAfterthrowing.class)) return  MtlAfterthrowing.class;
        return null;
    }

    private void di() throws IllegalAccessException {
        MyApplicationContextImpl applicationContext=MyApplicationContextImpl.getInstance();
        Map<String, Object> ioc = applicationContext.getIoc();
        if (ioc.size()==0) return;
        Iterator<Map.Entry<String, Object>> iterator = ioc.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            Object bean = entry.getValue();//IOC bean
            String beanName = entry.getKey();//IOC beanName
            Class<?> clazz = bean.getClass();
            Field[] declaredFields = clazz.getDeclaredFields();
            if (declaredFields==null||declaredFields.length==0) continue;
            for (Field declaredField : declaredFields) {
                if (!declaredField.isAnnotationPresent(MtlAutowired.class)) continue;
                MtlAutowired mtlAutowired = declaredField.getAnnotation(MtlAutowired.class);
                declaredField.setAccessible(true);
                Class<?> type = declaredField.getType();
                String name=type.getSimpleName();
                if (type.isInterface()) name=type.getName();
                if (!"".equals(mtlAutowired.value().trim())) name=mtlAutowired.value();
                Object obj = applicationContext.getBean(name);
                if (obj==null) throw new RuntimeException("not find bean! ["+name+"]");
                declaredField.set(bean, obj);
            }
            if (bean instanceof MyApplicationContextAware)
                ((MyApplicationContextAware) bean).setApplicationContext(applicationContext);
        }
    }

    private void newInstance() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (clazzs.size() == 0) return;
        for (String clazz : clazzs) {
            Class<?> aClass = Class.forName(clazz);
            if (!(aClass.isAnnotationPresent(MtlAspect.class) || aClass.isAnnotationPresent(MtlService.class)
                    || aClass.isAnnotationPresent(MtlController.class)))
                continue;
            String beanName=null;
            if (aClass.isAnnotationPresent(MtlAspect.class)) beanName=aClass.getAnnotation(MtlAspect.class).value();
            if (aClass.isAnnotationPresent(MtlController.class)) beanName=aClass.getAnnotation(MtlController.class).value();
            if (aClass.isAnnotationPresent(MtlService.class)) beanName=aClass.getAnnotation(MtlService.class).value();
            if ("".equals(beanName.trim())){
                beanName=clazz.substring(clazz.lastIndexOf(".")+1);
            }
            MyApplicationContextImpl applicationContext = MyApplicationContextImpl.getInstance();
            if (applicationContext.hasBean(beanName)) throw new RuntimeException("["+beanName+"] has be defined!");
            Object o = aClass.newInstance();
            applicationContext.setBean(beanName, o);
            //查看是否有接口
            Class<?>[] interfaces = aClass.getInterfaces();
            if (interfaces != null && interfaces.length > 0) {
                for (Class<?> anInterface : interfaces) {
                    String alias = anInterface.getName();
                    applicationContext.setAlias(beanName, alias);
                }
            }
            //解析AOP切面类
            Class<?> aopClazz = o.getClass();
            if (!aopClazz.isAnnotationPresent(MtlAspect.class)) continue;
            MtlAspect annotation = aopClazz.getAnnotation(MtlAspect.class);
            Method[] declaredMethods = aopClazz.getDeclaredMethods();
            if (declaredMethods==null||declaredMethods.length==0) continue;
            for (Method declaredMethod : declaredMethods) {
                if (!declaredMethod.isAnnotationPresent(MtlPointCut.class)) continue;
                MtlPointCut mtlPointCut = declaredMethod.getAnnotation(MtlPointCut.class);
                Class adviceClass = isAdvice(declaredMethod);
                if (adviceClass==null) continue;
                AopInvocation aopInvocation=new AopInvocation(o, declaredMethod, o.getClass().getName(), adviceClass);
                aopInvocation.setPointCut(mtlPointCut.value());
                applicationContext.addAopMapping(aopInvocation);
            }
        }
    }

    private void scanClass() throws MalformedURLException {
        Properties config = MyApplicationContextImpl.getInstance().getConfig();
        String basePackage = config.getProperty("basePackage");
        String path = "/" + basePackage.replaceAll("\\.", "/");
        doscanClass(path);
    }

    private void doscanClass(String path) {
        URL resource = getClass().getResource(path);
        File file = new File(resource.getFile());
        for (File f : file.listFiles()) {
            if (f.isDirectory()) {
                doscanClass(path + "/" + f.getName());
            } else {
                if (!f.getName().endsWith(".class")) {
                    continue;
                }
                String className = path.substring(1)+"/" + f.getName();
                int i = className.lastIndexOf(".");
                className = className.substring(0, i).replaceAll("/", ".");
                clazzs.add(className);
            }
        }
    }


    private void loadConfig(String configLocation) {
        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(configLocation);
        MyApplicationContextImpl.getInstance().setCongig(resourceAsStream);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
