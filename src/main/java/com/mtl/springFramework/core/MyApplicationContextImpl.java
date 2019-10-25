package com.mtl.springFramework.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 说明:IOC容器实现
 *
 * @作者 莫天龙
 * @时间 2019/10/24 9:28
 */
public final class MyApplicationContextImpl implements MyApplicationContext {
    /**
     * 容器
     */
    private Map<String,Object> ioc=new HashMap<>();

    private Map<String, List<String>> beanNameAliasMapping=new HashMap<>();

    /**
     * 请求URI和执行器的对应关系
     */
    private Map<String,MvcInvocation> requestMappings=new HashMap<>();

    /**
     * 切入点和和执行器的对应关系
     */
    private List<AopInvocation> aopMappings=new ArrayList<>();

    /**
     * 配置文件
     */
    private Properties config=new Properties();
    private static class ApplicationContextHolder {
        private static MyApplicationContextImpl instance = new MyApplicationContextImpl();
    }

    private MyApplicationContextImpl() {
    }

    static MyApplicationContextImpl getInstance() {
        return ApplicationContextHolder.instance;
    }

    @Override
    public Object getBean(String name) {
        Object o = ioc.get(name);
        if (o!=null) return o;
        //尝试用别名去取
        Iterator<Map.Entry<String, List<String>>> iterator = beanNameAliasMapping.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, List<String>> next = iterator.next();
            List<String> value = next.getValue();
            if (value==null) continue;
            if (value.contains(name)) {
                o=ioc.get(next.getKey());
                break;
            }
        }
        return o;
    }

    void setBean(String name, Object obj) {
        ioc.put(name, obj);
    }
    void setCongig(InputStream inputStream){
        try {
            config.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("load config failed!",e);
        }
    }
    Properties getConfig(){
        return config;
    }

    /**
     * 检查某个bean名字是否已经存在
     * @param beanName
     * @return
     */
    boolean hasBean(String beanName){
        return ioc.containsKey(beanName);
    }

    /**
     * 为bean设置别名
     * @param beanName
     * @param alias
     */
    void setAlias(String beanName,String alias){
        List<String> list = beanNameAliasMapping.get(beanName);
        if (list==null){
            list=new ArrayList<>();
            beanNameAliasMapping.put(beanName, list);
        }
        list.add(alias);
    }

    @Override
    public String[] getAllBeanNames(){
        Set<String> strings = ioc.keySet();
        return (String[]) strings.toArray();
    }

    Map<String,Object> getIoc(){
        return ioc;
    }

    /**
     * MVC添加映射关系
     * @param url
     * @param mvcInvocation
     */
    void addRequstMapping(String url,MvcInvocation mvcInvocation){
        if (requestMappings.containsKey(url)) throw new RuntimeException("url["+url+"] mapping is exists!");
        requestMappings.put(url, mvcInvocation);
    }

    /**
     * 添加AOP映射关系
     * @param aopInvocation
     */
    void addAopMapping(AopInvocation aopInvocation){
        aopMappings.add(aopInvocation);
    }

    List<AopInvocation> getAopMappings(){
        return aopMappings;
    }

    /**
     * 获取请求对应执行方法
     * @param url
     * @return
     */
    MvcInvocation getMvcInvocation(String url){
        return requestMappings.get(url);
    }

    @Override
    public String toString() {
        return "MyApplicationContextImpl{" +
                "ioc=" + ioc +
                ", requestMappings=" + requestMappings +
                ", aopMappings=" + aopMappings +
                '}';
    }
}
