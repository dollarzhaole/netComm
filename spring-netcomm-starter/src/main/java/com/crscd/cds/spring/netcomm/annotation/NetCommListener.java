package com.crscd.cds.spring.netcomm.annotation;

import java.lang.annotation.*;

/**
 * @author zhaole
 * @date 2022-04-25
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NetCommListener {
    short type() default 0x00;

    short func() default 0x00;
}
