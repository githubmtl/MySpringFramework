package com.mtl.springFramework.core;

/**
 * 说明:通过实现该接口，可获取ApplicationContext对象
 *
 * @作者 莫天龙
 * @时间 2019/10/25 17:46
 */
public interface MyApplicationContextAware {
    void setApplicationContext(MyApplicationContext applicationContext);
}
