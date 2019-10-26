# MySpringFramework
自己简单实现Spring IOC、MVC、AOP核心基本逻辑（AOP支持Cglib代理和JDK动态代理）
## 下面展示一下结果（创建一个WEB工程）
### 1.配置web.xml
```XML
<!DOCTYPE web-app PUBLIC
 "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN"
 "http://java.sun.com/dtd/web-app_2_3.dtd" >

<web-app>
  <display-name>MySpringFramework</display-name>
  <!-- 配置文件 -->
  <context-param>
    <param-name>configLocation</param-name>
    <param-value>config.properties</param-value>
  </context-param>
  <!-- 作用：启动时，初始化Bean,完成AOP增强，完成依赖注入 -->
  <listener>
    <listener-class>com.mtl.springFramework.core.MyContextLoaderListener</listener-class>
  </listener>
  <!-- 作用：拦截用户请求，转发到对应的方法 -->
  <servlet>
    <servlet-name>myMvc</servlet-name>
    <servlet-class>com.mtl.springFramework.core.MyDispatcherServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
  </servlet>
  <servlet-mapping>
    <servlet-name>myMvc</servlet-name>
    <url-pattern>/</url-pattern>
  </servlet-mapping>
  <welcome-file-list>
    <welcome-file>index.jsp</welcome-file>
  </welcome-file-list>
</web-app>
```
配置文件中的listener和DispatcherServlet均为自己实现的，而非spring中的类，其中context-param传入了一个properties文件，放在classpath目录下即可。内容如下：
```java
basePackage=com.mtl.test
```
该配置文件配置了我们需要扫描的类的包名,即该包名下的所有类，只要加了我们自定义的注解，则我们就将其加入我们自己实现的IOC容器中，并完成依赖注入。

### 2.写一个模拟dao层
```java
package com.mtl.test.dao;

/**
 * 说明:DAO层测试接口
 *
 * @作者 莫天龙
 * @时间 2019/10/24 12:06
 */
public interface ITestDao {
    int save(String s);
    String queryAll(String name);
}
```
一共两个方法，一个保存，一个查询。下面写一个实现类
```java
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
```
实现和简单，皆打印一句话，并加一个自己写的注解@MtlService，表示该类交给我们自己实现的IOC容器管理

### 3.写一个测试服务类
```JAVA
ackage com.mtl.test.service;

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
        System.out.println("doit....");
        return 1;
    }
}
```
注意：该服务类并没有接口，同样加一个自己的注解@MtlService，表示该类交给我们自己实现的IOC容器管理，该类主要用于测试通过Cglib动态代理实现的AOP

### 4.写一个服务测试接口
```java
package com.mtl.test.service;

/**
 * 说明:测试服务接口
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:31
 */
public interface ITestService {
    public String test(String name);
}

```
很简单，就一个test方法，下面写一个他的实现类
```java
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
```
同样加一个自己写的注解@MtlService，表示该类交给我们自己实现的IOC容器管理，并且有两个成员变量，一个是之前写的DAO层测试接口，一个是刚刚写的没有接口的Service,他们都有同样的一个注解@MtlAutowired,表示由我们自己写的IOC容器自动注入
该类主要用于测试由JDK动态代理实现的AOP

### 5.写一个测试controller
```java
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
```
注意：参数只能是HttpServletRequest request和HttpServletResponse response,没有像SpringMVC一样做了参数绑定。（类中的注解也是自己写的注解，并非Spring里面的注解），并且自动注入了刚刚有接口的service

### 6.最后写一个AOP切面类
```java
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

```
@MtlAspect注解和Spring中的@Aspect注解作用相同，表示该类是一个切面类，@MtlPointCut和spring中的@pointCut注解作用相同，表示切入点，只是没有Srping中的强大，里面配置一个方法的全路径（完整类名+"."+方法）
@MtlBefore、 @MtlAfter、 @MtlAfterReturnning、@MtlAfterthrowing注解见名思意，表示前置通知、最终通知、返回通知和异常通知。
该类配置了如下增强：在类com.mtl.test.service.MyService的方法doit执行前，打印一句before,在执行完并返回值后打印一句话afterReturnning...，并且把返回值打印出来，接着在打印after（@MtlAfter注解表示不管正常返回还是方法异常了，都会增强）。
在类com.mtl.test.dao.TestDao的方法save执行前模拟事务开始，正常返回后模拟事务提交，异常后，模拟事务回滚

### 7.配置完成，启动tomcat，在浏览器中输入http://localhost:8080/mymvc/test?name=123456  可以在浏览器中看到以下内容
```java
i'm work! get the parameter:123456
```
并且可以看到在控制台看到以下内容
```java
before...
doit....
afterReturnning... return obj=1
after...
模拟开始事务！
save....
模拟提交事务！ 方法返回值:1
query all...
```
看到配置了AOP的方法已经得到了增强

完毕~~~
