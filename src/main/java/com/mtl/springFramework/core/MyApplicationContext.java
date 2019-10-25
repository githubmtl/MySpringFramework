package com.mtl.springFramework.core;

/**
 * 说明:IOC容器接口
 *
 * @作者 莫天龙
 * @时间 2019/10/24 9:25
 */
public interface MyApplicationContext {
    /**
     * 获取bean
     * @param name
     * @return
     */
    Object getBean(String name);

    /**
     * 获取所有bean的名称
     * @return
     */
    String[] getAllBeanNames();
}
