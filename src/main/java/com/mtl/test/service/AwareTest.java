package com.mtl.test.service;

import com.mtl.springFramework.annotation.MtlService;
import com.mtl.springFramework.core.MyApplicationContext;
import com.mtl.springFramework.core.MyApplicationContextAware;

/**
 * 说明:测试获取ApplicationContext
 *
 * @作者 莫天龙
 * @时间 2019/10/25 17:50
 */
@MtlService
public class AwareTest implements MyApplicationContextAware {
    public static MyApplicationContext applicationContext;
    @Override
    public void setApplicationContext(MyApplicationContext applicationContext) {
        AwareTest.applicationContext=applicationContext;
    }
}
