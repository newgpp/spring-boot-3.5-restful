package com.felix.demo;

import com.felix.demo.schema.QueryParser;
import com.felix.demo.schema.db.SchemaRegistry;
import com.felix.demo.schema.restful.FilterSpec;
import com.felix.demo.schema.restful.QuerySpec;
import com.felix.demo.schema.restful.ResourceSchema;
import com.felix.demo.schema.restful.SortSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryParserTest {

    private ResourceSchema userSchema;

    @BeforeEach
    void setUp() throws Exception {
        SchemaRegistry registry = new SchemaRegistry();
        registry.afterPropertiesSet();
        userSchema = registry.get("users");
    }

    @Test
    void make_parse_with_filters_sorts_and_fields_should_success() {
        // given
        QuerySpec spec = QueryParser.parse(
                "age>20;username~john",
                "-age,username",
                "id,username,age",
                userSchema
        );

        // then
        assertThat(spec.getFilters())
                .hasSize(2)
                .extracting(FilterSpec::getField)
                .containsExactly("age", "username");
        assertThat(spec.getFilters().get(0).getValue()).isInstanceOf(Integer.class);
        assertThat(spec.getFilters().get(0).getValue()).isEqualTo(20);

        assertThat(spec.getSorts())
                .hasSize(2)
                .extracting(SortSpec::isAsc)
                .containsExactly(false, true);

        assertThat(spec.getSelectFields())
                .containsExactly("id", "username", "age");
    }

    @Test
    void do_parse_with_non_filterable_field_should_throw() {
        // when & then
        assertThatThrownBy(() -> QueryParser.parse(
                "ext_json=something",
                null,
                null,
                userSchema
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("filterable");
    }

    @Test
    void do_parse_with_non_sortable_field_should_throw() {
        // when & then
        assertThatThrownBy(() -> QueryParser.parse(
                null,
                "password",
                null,
                userSchema
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sortable");
    }
}
