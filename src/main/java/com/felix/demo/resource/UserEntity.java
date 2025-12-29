package com.felix.demo.resource;

import com.felix.demo.schema.db.RestField;
import com.felix.demo.schema.db.RestId;
import com.felix.demo.schema.db.RestResource;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@RestResource(value = "users", table = "t_user")
@Data
public class UserEntity {

    @RestId
    @RestField
    private String id;

    @RestField
    private String username;

    @RestField(sortable = false)
    private String password; // 不允许排序，但可查询（或直接 selectable=false）

    @RestField
    private Integer age;

    @RestField(
            filterable = false,
            sortable = false,
            selectable = false
    )
    private Map<String, Object> ext_json;

    @RestField(filterable = false)
    private LocalDateTime create_time;
}
