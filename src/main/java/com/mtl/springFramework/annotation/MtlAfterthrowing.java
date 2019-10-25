package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:前置通知配置注解
 *
 * @作者 莫天龙
 * @时间 2019/10/24 18:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MtlAfterthrowing {
}
