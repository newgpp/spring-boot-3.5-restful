package com.felix.demo.schema.db;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FieldSchema {
    private String column;
    private boolean selectable;
    private boolean sortable;
    private boolean filterable;
    private Class<?> javaType;
}
