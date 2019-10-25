package com.mtl.test.dao;

import com.mtl.springFramework.annotation.MtlService;

/**
 * 说明:DAO层测试实现类
 *
 * @作者 莫天龙
 * @时间 2019/10/24 12:08
 */
@MtlService
public class TestDao implements ITestDao {
    @Override
    public int save(String s) {
        System.out.println("save....");
        return 1;
    }

    @Override
    public String queryAll(String name) {
        System.out.println("query all...");
        return "i'm work! get the parameter:"+name;
    }
}
