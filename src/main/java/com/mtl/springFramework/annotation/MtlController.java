package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:IOC前端控制器类注释
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:42
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface MtlController {
    String value() default "";
}
