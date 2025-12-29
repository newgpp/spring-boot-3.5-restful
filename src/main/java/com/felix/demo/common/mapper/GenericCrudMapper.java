package com.felix.demo.common.mapper;

import com.felix.demo.schema.restful.QuerySpec;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface GenericCrudMapper {

    List<Map<String, Object>> selectPage(
            @Param("table") String table,
            @Param("offset") int offset,
            @Param("limit") int limit,
            @Param("spec") QuerySpec spec
    );

    Map<String, Object> selectById(
            @Param("table") String table,
            @Param("id") Long id
    );

    int insert(
            @Param("table") String table,
            @Param("data") Map<String, Object> data
    );

    int update(
            @Param("table") String table,
            @Param("id") Long id,
            @Param("data") Map<String, Object> data
    );

    int delete(
            @Param("table") String table,
            @Param("id") Long id
    );

    int count(@Param("table") String table,
              @Param("spec") QuerySpec spec
    );
}
