package com.felix.demo.schema;

import com.felix.demo.schema.db.FieldSchema;
import com.felix.demo.schema.restful.*;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class QueryParser {

    private QueryParser() {
    }

    public static QuerySpec parse(
            String filter,
            String sort,
            String fields,
            ResourceSchema schema) {

        QuerySpec spec = new QuerySpec();

        parseFilter(filter, schema, spec);
        parseSort(sort, schema, spec);
        parseFields(fields, schema, spec);

        return spec;
    }

    /* ===================== filter ===================== */

    private static void parseFilter(
            String filter,
            ResourceSchema schema,
            QuerySpec spec) {

        if (!StringUtils.hasText(filter)) {
            return;
        }

        List<FilterSpec> filterSpecs = new ArrayList<>();
        for (String exp : filter.split(";")) {
            FilterSpec f = parseOne(exp.trim());
            FieldSchema fs = schema.getFields().get(f.getField());
            if (fs == null || !fs.isFilterable()) {
                throw new IllegalArgumentException(
                        "Field not filterable: " + f.getField());
            }
            // 类型转换（核心）
            Object converted = convert(f.getValue(), fs.getJavaType());
            f.setValue(converted);
            filterSpecs.add(f);
        }
        spec.setFilters(filterSpecs);
    }

    private static FilterSpec parseOne(String exp) {
        if (exp.contains(">=")) return split(exp, ">=", Operator.GE);
        if (exp.contains("<=")) return split(exp, "<=", Operator.LE);
        if (exp.contains("!=")) return split(exp, "!=", Operator.NE);
        if (exp.contains(">")) return split(exp, ">", Operator.GT);
        if (exp.contains("<")) return split(exp, "<", Operator.LT);
        if (exp.contains("~")) return split(exp, "~", Operator.LIKE);
        if (exp.contains("=")) return split(exp, "=", Operator.EQ);

        throw new IllegalArgumentException("Invalid filter: " + exp);
    }

    private static FilterSpec split(
            String exp, String symbol, Operator op) {

        String[] arr = exp.split(symbol, 2);
        if (arr.length != 2) {
            throw new IllegalArgumentException("Invalid filter: " + exp);
        }
        return new FilterSpec(arr[0].trim(), op, arr[1].trim());
    }

    /* ===================== sort ===================== */

    private static void parseSort(
            String sort,
            ResourceSchema schema,
            QuerySpec spec) {

        if (!StringUtils.hasText(sort)) {
            return;
        }
        List<SortSpec> sortSpecs = new ArrayList<>();
        for (String s : sort.split(",")) {
            boolean asc = !s.startsWith("-");
            String field = asc ? s : s.substring(1);
            FieldSchema fs = schema.getFields().get(field);
            if (fs == null || !fs.isSortable()) {
                throw new IllegalArgumentException(
                        "Field not sortable: " + field);
            }
            sortSpecs.add(new SortSpec(field, asc));
        }
        spec.setSorts(sortSpecs);
    }

    /* ===================== fields ===================== */

    private static void parseFields(
            String fields,
            ResourceSchema schema,
            QuerySpec spec) {

        if (!StringUtils.hasText(fields)) {
            return;
        }

        List<String> selectFields = new ArrayList<>();

        for (String f : fields.split(",")) {
            FieldSchema fs = schema.getFields().get(f);
            if (fs == null || !fs.isSelectable()) {
                throw new IllegalArgumentException(
                        "Field not selectable: " + f);
            }
            selectFields.add(f);
        }

        spec.setSelectFields(selectFields);
    }

    /* ===================== type convert ===================== */

    private static Object convert(Object raw, Class<?> targetType) {
        if (raw == null) {
            return null;
        }

        String val = raw.toString();

        if (targetType == String.class) {
            return val;
        }
        if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(val);
        }
        if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(val);
        }
        if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(val);
        }
        if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(val);
        }
        if (targetType == LocalDate.class) {
            return LocalDate.parse(val);
        }
        if (targetType == LocalDateTime.class) {
            return LocalDateTime.parse(val);
        }
        if (targetType.isEnum()) {
            return Enum.valueOf((Class<Enum>) targetType, val);
        }

        throw new IllegalArgumentException(
                "Unsupported filter type: " + targetType.getName());
    }
}
