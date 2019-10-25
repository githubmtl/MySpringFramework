package com.mtl.test.controller;

import com.mtl.springFramework.annotation.MtlAutowired;
import com.mtl.springFramework.annotation.MtlController;
import com.mtl.springFramework.annotation.MtlRquestMapping;
import com.mtl.test.service.ITestService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 说明:MVC测试类
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:25
 */
@MtlController
@MtlRquestMapping(("/mymvc"))
public class TestController {
    @MtlAutowired
    private ITestService iTestService;
    @MtlRquestMapping("/test")
    public String test(HttpServletRequest request, HttpServletResponse response){
        return iTestService.test(request.getParameter("name"));
    }
}
