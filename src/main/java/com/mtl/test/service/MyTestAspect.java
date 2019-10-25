package com.mtl.test.service;

import com.mtl.springFramework.annotation.*;

/**
 * 说明:测试切面类
 *
 * @作者 莫天龙
 * @时间 2019/10/24 19:13
 */
@MtlAspect
public class MyTestAspect {
    @MtlBefore
    @MtlPointCut("com.mtl.test.service.MyService.doit")
    public void before(){
        System.out.println("before...");
    }
    @MtlAfter
    @MtlPointCut("com.mtl.test.service.MyService.doit")
    public void after(){
        System.out.println("after...");
    }
    @MtlAfterReturnning
    @MtlPointCut("com.mtl.test.service.MyService.doit")
    public void afterReturnning(Object object){
        System.out.println("afterReturnning... return obj="+object);
    }
    @MtlBefore
    @MtlPointCut("com.mtl.test.dao.TestDao.save")
    public void txBefore(){
        System.out.println("模拟开始事务！");
    }
    @MtlAfterReturnning
    @MtlPointCut("com.mtl.test.dao.TestDao.save")
    public void txCommit(Object retObj){
        System.out.println("模拟提交事务！ 方法返回值:"+retObj);
    }
    @MtlAfterthrowing
    @MtlPointCut("com.mtl.test.dao.TestDao.save")
    public void txRollBack(Throwable t){
        System.out.println("模拟回滚事务！  t="+t);
    }

    //普通方法
    public void sayHello(){
        System.out.println("hello!");
    }
}
