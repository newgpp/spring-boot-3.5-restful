package com.felix.demo.executor;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.felix.demo.common.vo.PageResult;
import com.felix.demo.executor.convertor.TypeConvertor;
import com.felix.demo.schema.restful.QuerySpec;
import com.felix.demo.schema.restful.ResourceSchema;

@Service
@ConditionalOnProperty(name = "app.datasource", havingValue = "mongo")
public class MongoQueryExecutor implements GenericQueryExecutor {

    @Autowired
    private MongoTemplate mongoTemplate;

    /* ======================= 查询 ======================= */

    @Override
    public PageResult list(ResourceSchema schema, QuerySpec spec) {
        Query query = buildQuery(spec);
        List<Document> list = mongoTemplate.find(query, Document.class, schema.getCollection());
        List<Map<String, Object>> records = list.stream().map(MongoQueryExecutor::convert).toList();
        return new PageResult(records, records.size());
    }

    @Override
    public PageResult page(ResourceSchema schema, int page, int size, QuerySpec spec) {
        Query query = buildQuery(spec);
        long total = mongoTemplate.count(query, schema.getCollection());
        query.skip((long) (page - 1) * size).limit(size);
        List<Document> list = mongoTemplate.find(query, Document.class, schema.getCollection());
        List<Map<String, Object>> records = list.stream().map(MongoQueryExecutor::convert).toList();
        return new PageResult(new ArrayList<>(records), total);
    }

    /* ======================= 单条 ======================= */

    @Override
    public Map<String, Object> get(ResourceSchema schema, Object id) {
        Object realId = id2ObjectId(id);
        Document doc = mongoTemplate.findById(realId, Document.class, schema.getCollection());
        return convert(doc);
    }

    /* ======================= 新增 ======================= */

    @Override
    public int insert(ResourceSchema schema, Map<String, Object> data) {
        Document doc = TypeConvertor.convertToBson(schema, data);
        mongoTemplate.insert(doc, schema.getCollection());
        return 1;
    }

    /* ======================= 更新 ======================= */

    @Override
    public int update(ResourceSchema schema, Object id, Map<String, Object> data) {
        Object realId = id2ObjectId(id);

        Document doc = TypeConvertor.convertToBson(schema, data);

        Update update = new Update();
        doc.forEach((k, v) -> {
            if (!"_id".equals(k)) {
                update.set(k, v);
            }
        });

        return (int) mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(realId)),
                update,
                schema.getCollection()
        ).getModifiedCount();
    }

    /* ======================= 删除 ======================= */

    @Override
    public int delete(ResourceSchema schema, Object id) {
        Object realId = id2ObjectId(id);
        return (int) mongoTemplate
                .remove(
                        Query.query(Criteria.where("_id").is(realId)),
                        schema.getCollection()
                ).getDeletedCount();
    }

    /* ======================= 工具 ======================= */

    private Object id2ObjectId(Object id) {
        if (id instanceof String s && ObjectId.isValid(s)) {
            return new ObjectId(s);
        }
        return id;
    }

    public static Map<String, Object> convert(Document doc) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : doc.entrySet()) {
            Object value = entry.getValue();

            // 1. 处理日期类型
            if (value instanceof Date) {
                value = ((Date) value)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
            // 2. 处理 ObjectId 类型
            else if (value instanceof ObjectId) {
                value = ((ObjectId) value).toHexString();
            }
            // 3. 处理数字类型（解决 45.0 变 45 的问题）
            else if (value instanceof Double) {
                Double d = (Double) value;
                // 检查是否为整数（即小数位为 0）
                if (d % 1 == 0) {
                    // 如果在 Integer 范围内转为 Integer，否则转为 Long
                    if (d >= Integer.MIN_VALUE && d <= Integer.MAX_VALUE) {
                        value = d.intValue();
                    } else {
                        value = d.longValue();
                    }
                }
            }
            // 4. 递归处理嵌套的 Document (如 ext_json)
            else if (value instanceof Document) {
                value = convert((Document) value);
            }

            map.put(entry.getKey(), value);
        }

        if (map.containsKey("_id")) {
            map.put("id", map.remove("_id"));
        }

        return map;
    }

    private Query buildQuery(QuerySpec spec) {
        Query q = new Query();
        if (spec.getFilters() != null) {
            spec.getFilters().forEach(f -> {
                Criteria c = Criteria.where(f.getField());
                switch (f.getOperator()) {
                    case EQ -> c.is(f.getValue());
                    case NE -> c.ne(f.getValue());
                    case GT -> c.gt(f.getValue());
                    case GE -> c.gte(f.getValue());
                    case LT -> c.lt(f.getValue());
                    case LE -> c.lte(f.getValue());
                    case LIKE -> c.regex(f.getValue().toString(), "i");
                }
                q.addCriteria(c);
            });
        }
        if (spec.getSorts() != null) {
            spec.getSorts().forEach(s ->
                    q.with(s.isAsc()
                            ? Sort.by(s.getField()).ascending()
                            : Sort.by(s.getField()).descending())
            );
        }
        if (spec.getSelectFields() != null) {
            spec.getSelectFields()
                    .forEach(f -> q.fields().include(f));
        }
        return q;
    }








}

