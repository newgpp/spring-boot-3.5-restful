package com.felix.demo.executor;

import com.felix.demo.common.vo.PageResult;
import com.felix.demo.schema.restful.QuerySpec;
import com.felix.demo.schema.restful.ResourceSchema;

import java.util.Map;

public interface GenericQueryExecutor {

    PageResult list(ResourceSchema schema, QuerySpec spec);

    PageResult page(ResourceSchema schema, int page, int size, QuerySpec spec);

    Map<String, Object> get(ResourceSchema schema, Object id);

    int insert(ResourceSchema schema, Map<String, Object> data);

    int update(ResourceSchema schema, Object id, Map<String, Object> data);

    int delete(ResourceSchema schema, Object id);
}
