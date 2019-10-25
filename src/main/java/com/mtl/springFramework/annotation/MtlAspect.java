package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:AOP配置注释
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:44
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MtlAspect {
    String value() default "";
}
