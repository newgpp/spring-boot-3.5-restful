package com.felix.demo.executor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.felix.demo.common.mapper.GenericCrudMapper;
import com.felix.demo.common.vo.PageResult;
import com.felix.demo.executor.convertor.TypeConvertor;
import com.felix.demo.schema.db.FieldSchema;
import com.felix.demo.schema.restful.QuerySpec;
import com.felix.demo.schema.restful.ResourceSchema;

@Service
@ConditionalOnProperty(name = "app.datasource", havingValue = "mysql")
public class MySqlQueryExecutor implements GenericQueryExecutor {

    @Autowired
    private GenericCrudMapper mapper;

    @Override
    public PageResult list(ResourceSchema schema, QuerySpec spec) {
        List<Map<String, Object>> list =
                mapper.selectPage(schema.getTable(), 0, 0, spec);
        List<Map<String, Object>> result =
                list.stream()
                        .map(row -> convertFromSql(schema, row))
                        .toList();
        return new PageResult(result, list.size());
    }

    @Override
    public PageResult page(ResourceSchema schema, int page, int size, QuerySpec spec) {
        int offset = (page - 1) * size;
        List<Map<String, Object>> list =
                mapper.selectPage(schema.getTable(), offset, size, spec);
        List<Map<String, Object>> result =
                list.stream()
                        .map(row -> convertFromSql(schema, row))
                        .toList();
        int total = mapper.count(schema.getTable(), spec);
        return new PageResult(result, total);
    }

    @Override
    public Map<String, Object> get(ResourceSchema schema, Object id) {
        Map<String, Object> line = mapper.selectById(schema.getTable(), convertId(id));
        return convertFromSql(schema, line);
    }

    @Override
    public int insert(ResourceSchema schema, Map<String, Object> data) {
        Map<String, Object> line = convertForSql(schema, data);
        return mapper.insert(schema.getTable(), line);
    }

    @Override
    public int update(ResourceSchema schema, Object id, Map<String, Object> data) {
        Map<String, Object> line = convertForSql(schema, data);
        return mapper.update(schema.getTable(), convertId(id), line);
    }

    @Override
    public int delete(ResourceSchema schema, Object id) {
        return mapper.delete(schema.getTable(), convertId(id));
    }

    /**
     * MySQL 主键适配（核心）
     */
    private Long convertId(Object id) {
        if (id instanceof String s && s.matches("\\d+")) {
            return Long.valueOf(s);
        } else {
            throw new IllegalArgumentException("mysql id only support long type");
        }
    }

    private Map<String, Object> convertForSql(
            ResourceSchema schema,
            Map<String, Object> data
    ) {
        Map<String, Object> result = new HashMap<>();

        data.forEach((k, v) -> {
            FieldSchema field = schema.getFields().get(k);
            if (field == null) return;

            Object jdbcValue =
                    TypeConvertor.toJdbcValue(v, field.getJavaType());

            result.put(field.getColumn(), jdbcValue);
        });

        return result;
    }

    private Map<String, Object> convertFromSql(
            ResourceSchema schema,
            Map<String, Object> row
    ) {
        Map<String, Object> result = new HashMap<>();

        row.forEach((column, value) -> {

            FieldSchema field = schema.getFields().get(column);
            if (field == null || value == null) {
                result.put(column, value);
                return;
            }

            Class<?> targetType = field.getJavaType();

            // ===== JSON =====
            if (targetType == Map.class || targetType == List.class) {
                result.put(column, TypeConvertor.parseJson(value));
                return;
            }

            // 默认
            result.put(column, value);
        });

        return result;
    }





}

