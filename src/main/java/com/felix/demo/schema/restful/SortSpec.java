package com.felix.demo.schema.restful;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SortSpec {
    private String field;
    private boolean asc;
}
