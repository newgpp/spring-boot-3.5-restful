package com.felix.demo.schema.restful;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterSpec {
    private String field;
    private Operator operator;
    private Object value;
}
