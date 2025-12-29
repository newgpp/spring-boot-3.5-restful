package com.felix.demo.schema.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestResource {
    String value();      // resource 名，如 users
    String table();      // 表名，如 t_user
}
