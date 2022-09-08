package com.douyiyuan.first.common;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReleaseAnnotation {
    String name() default "";
}
