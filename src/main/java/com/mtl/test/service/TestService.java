package com.mtl.test.service;

import com.mtl.springFramework.annotation.MtlAutowired;
import com.mtl.springFramework.annotation.MtlService;
import com.mtl.springFramework.core.MyApplicationContextImpl;
import com.mtl.test.dao.ITestDao;

/**
 * 说明:测试服务类（JDK动态代理）
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:30
 */
@MtlService
public class TestService implements ITestService {
    @MtlAutowired
    private ITestDao iTestDao;
    @MtlAutowired
    private MyService service;

    @Override
    public String test(String name) {
        service.doit();
        iTestDao.save("1212");
        return iTestDao.queryAll(name);
    }
}
