package com.crscd.cds.spring.netcomm.annotation;

import com.crscd.cds.spring.netcomm.config.NetCommAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author zhaole
 * @date 2022-04-25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({NetCommAutoConfiguration.class})
public @interface EnableNetComm {}
