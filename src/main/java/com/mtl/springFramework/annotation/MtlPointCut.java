package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:AOP切入点配置
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:45
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface MtlPointCut {
    String value() default "";
}
