package com.felix.demo.schema.db;

import com.felix.demo.schema.restful.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Component
public class SchemaRegistry implements InitializingBean {

    private final Map<String, ResourceSchema> schemas = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        scan("com.felix.demo.resource");
    }

    public ResourceSchema get(String resource){
        if (resource == null || !schemas.containsKey(resource)) {
            throw new IllegalArgumentException("illegal resource name");
        }
        return schemas.get(resource);
    }

    public String getTable(String resource) {
        if (resource == null || !schemas.containsKey(resource)) {
            throw new IllegalArgumentException("illegal resource name");
        }
        return schemas.get(resource).getTable();
    }


    private void scan(String basePackage) throws Exception {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(RestResource.class));

        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            Class<?> clazz = Class.forName(bd.getBeanClassName());
            buildSchema(clazz);
        }
    }

    private void buildSchema(Class<?> clazz) {
        RestResource rr = clazz.getAnnotation(RestResource.class);

        ResourceSchema schema = new ResourceSchema();
        schema.setResource(rr.value());
        schema.setTable(rr.table());
        schema.setCollection(rr.table());

        for (Field f : clazz.getDeclaredFields()) {
            if (f.isAnnotationPresent(RestId.class)) {
                schema.setIdField(f.getName());
            }

            RestField rf = f.getAnnotation(RestField.class);
            if (rf != null) {
                schema.getFields().put(
                        f.getName(),
                        new FieldSchema(
                                f.getName(),
                                rf.selectable(),
                                rf.sortable(),
                                rf.filterable(),
                                f.getType()
                        )
                );
            }
        }

        schemas.put(schema.getResource(), schema);
    }
}
