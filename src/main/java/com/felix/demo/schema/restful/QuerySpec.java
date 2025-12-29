package com.felix.demo.schema.restful;

import lombok.Data;

import java.util.List;

@Data
public class QuerySpec {
    private List<String> selectFields;
    private List<SortSpec> sorts;
    private List<FilterSpec> filters;
}
