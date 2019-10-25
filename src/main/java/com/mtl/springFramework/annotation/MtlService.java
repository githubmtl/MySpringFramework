package com.mtl.springFramework.annotation;

import java.lang.annotation.*;

/**
 * 说明:IOC服务类注释
 *
 * @作者 莫天龙
 * @时间 2019/10/24 10:41
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface MtlService {
    String value() default "";
}
