package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:IOC依赖注入配置注解
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:49
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MtlAutowired {
    String value() default "";
}
