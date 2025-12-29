package com.felix.demo.executor.convertor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felix.demo.schema.db.FieldSchema;
import com.felix.demo.schema.restful.ResourceSchema;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TypeConvertor {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static Object toJdbcValue(Object value, Class<?> targetType) {
        if (value == null) return null;

        // ===== 日期 =====
        if (targetType == LocalDateTime.class) {
            return parseToTimestamp(value);
        }

        if (targetType == LocalDate.class) {
            return parseToDate(value);
        }

        // ===== 数字 =====
        if (targetType == Long.class) {
            return Long.valueOf(value.toString());
        }
        if (targetType == Integer.class) {
            return Integer.valueOf(value.toString());
        }
        if (targetType == Double.class) {
            return Double.valueOf(value.toString());
        }

        // ===== Boolean =====
        if (targetType == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }

        // ===== JSON / Map =====
        if (value instanceof Map || value instanceof List) {
            String json = null;
            try {
                json = mapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                //ignore
            }
            return json; // 建议 JSON 存 MySQL
        }

        return value;
    }

    private static Timestamp parseToTimestamp(Object value) {
        if (value instanceof Timestamp t) return t;

        if (value instanceof Date d) {
            return new Timestamp(d.getTime());
        }

        if (value instanceof Long l) {
            return new Timestamp(l);
        }

        if (value instanceof String s) {
            LocalDateTime ldt = LocalDateTime.parse(
                    s.replace(" ", "T")
            );
            return Timestamp.valueOf(ldt);
        }

        throw new IllegalArgumentException("无法解析时间: " + value);
    }

    public static Document convertToBson(ResourceSchema schema, Map<String, Object> data) {
        Document doc = new Document();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            FieldSchema fs = schema.getFields().get(field);
            if (fs == null || value == null) {
                continue;
            }

            Object bsonValue = convertToBsonField(value, fs.getJavaType());
            doc.put(fs.getColumn(), bsonValue);
        }

        return doc;
    }

    private static Object convertToBsonField(Object value, Class<?> targetType) {

        if (value == null) return null;

        // ===== 日期处理（重点）=====
        if (targetType == LocalDateTime.class) {
            return parseToLocalDateTime(value);
        }

        if (targetType == Date.class) {
            return parseToDate(value);
        }

        // ===== ObjectId =====
        if (targetType == ObjectId.class) {
            if (value instanceof String s && ObjectId.isValid(s)) {
                return new ObjectId(s);
            }
        }

        // ===== 数字 =====
        if (targetType == Integer.class) {
            return Integer.valueOf(value.toString());
        }
        if (targetType == Long.class) {
            return Long.valueOf(value.toString());
        }
        if (targetType == Double.class) {
            return Double.valueOf(value.toString());
        }

        // ===== Boolean =====
        if (targetType == Boolean.class) {
            return Boolean.valueOf(value.toString());
        }

        // ===== Map / 嵌套 =====
        if (value instanceof Map<?, ?> map) {
            return new Document((Map<String, ?>) map);
        }

        // 默认
        return value;
    }

    private static LocalDateTime parseToLocalDateTime(Object value) {

        if (value instanceof LocalDateTime ldt) {
            return ldt;
        }

        if (value instanceof Date d) {
            return d.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        if (value instanceof Long ts) {
            return Instant.ofEpochMilli(ts)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        if (value instanceof String s) {
            s = s.trim();

            // 时间戳字符串
            if (s.matches("\\d+")) {
                return Instant.ofEpochMilli(Long.parseLong(s))
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }

            // 常见格式
            DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME,
                    DateTimeFormatter.ISO_LOCAL_DATE
            };

            for (DateTimeFormatter f : formatters) {
                try {
                    return LocalDateTime.parse(s, f);
                } catch (Exception ignored) {}
            }
        }

        throw new IllegalArgumentException("无法解析时间字段: " + value);
    }

    private static Date parseToDate(Object value) {
        LocalDateTime ldt = parseToLocalDateTime(value);
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Object parseJson(Object value) {
        if (value == null) {
            return null;
        }

        // 已经是 Map / List，直接返回
        if (value instanceof Map || value instanceof List) {
            return value;
        }

        // MySQL JSON 字段通常是 String
        if (value instanceof String json) {
            json = json.trim();
            if (json.isEmpty()) return null;

            try {
                // 判断是对象还是数组
                if (json.startsWith("{")) {
                    return mapper.readValue(
                            json, new TypeReference<Map<String, Object>>() {});
                }
                if (json.startsWith("[")) {
                    return mapper.readValue(
                            json, new TypeReference<List<Object>>() {});
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("JSON 解析失败: " + json, e);
            }
        }

        // 兜底
        return value;
    }

}
