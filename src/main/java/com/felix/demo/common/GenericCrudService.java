package com.felix.demo.common;

import com.felix.demo.common.vo.PageResult;
import com.felix.demo.executor.GenericQueryExecutor;
import com.felix.demo.schema.QueryParser;
import com.felix.demo.schema.db.SchemaRegistry;
import com.felix.demo.schema.restful.QuerySpec;
import com.felix.demo.schema.restful.ResourceSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GenericCrudService {

    @Autowired
    private GenericQueryExecutor executor;

    @Autowired
    private SchemaRegistry schemaRegistry;

    public PageResult list(String resource, String filter, String sort, String fields) {
        ResourceSchema schema = schemaRegistry.get(resource);
        QuerySpec spec = QueryParser.parse(filter, sort, fields, schema);
        return executor.list(schema, spec);
    }

    public PageResult page(String resource, int page, int size,
                           String filter, String sort, String fields) {
        ResourceSchema schema = schemaRegistry.get(resource);
        QuerySpec spec = QueryParser.parse(filter, sort, fields, schema);
        return executor.page(schema, page, size, spec);
    }

    public Map<String, Object> get(String resource, String id) {
        return executor.get(schemaRegistry.get(resource), id);
    }

    public int create(String resource, Map<String, Object> body) {
        return executor.insert(schemaRegistry.get(resource), body);
    }

    public int update(String resource, String id, Map<String, Object> body) {
        return executor.update(schemaRegistry.get(resource), id, body);
    }

    public int delete(String resource, String id) {
        return executor.delete(schemaRegistry.get(resource), id);
    }
}
