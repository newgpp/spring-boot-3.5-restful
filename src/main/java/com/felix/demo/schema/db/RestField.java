package com.felix.demo.schema.db;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestField {

    boolean selectable() default true;  // 是否可出现在 fields
    boolean sortable() default true;    // 是否可 sort
    boolean filterable() default true;  // 是否可 filter
}
