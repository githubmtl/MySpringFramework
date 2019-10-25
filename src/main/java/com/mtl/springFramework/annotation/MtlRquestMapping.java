package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:MVC映射配置注解
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:47
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Documented
public @interface MtlRquestMapping {
    String value() default "";
}
