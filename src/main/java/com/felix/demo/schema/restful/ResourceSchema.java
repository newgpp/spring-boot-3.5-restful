package com.felix.demo.schema.restful;

import com.felix.demo.schema.db.FieldSchema;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ResourceSchema {
    private String resource;
    private String table;
    private String collection;
    private String idField;
    private Map<String, FieldSchema> fields = new HashMap<>();
}
