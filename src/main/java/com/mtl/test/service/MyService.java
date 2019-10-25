package com.mtl.test.service;

import com.mtl.springFramework.annotation.MtlService;

/**
 * 说明:测试服务类（测试CGlib代理）
 *
 * @作者 莫天龙
 * @时间 2019/10/24 17:01
 */
@MtlService
public class MyService {
    public int doit(){
        System.out.println("获取applicatinContext:"+AwareTest.applicationContext);
        System.out.println("doit....");
        return 1;
    }
}
