package org.dromara.common.json.utils;

import static org.assertj.core.api.Assertions.assertThat;

import cn.hutool.core.lang.Dict;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/** JsonUtils (JSON工具类) 单元测试 */
@DisplayName("JsonUtils (JSON工具类) 单元测试")
class JsonUtilsTest {

    @BeforeAll
    static void setUpAll() {
        // 使用 setObjectMapper 方法注入测试用的 ObjectMapper
        JsonUtils.setObjectMapper(new ObjectMapper());
    }

    @Nested
    @DisplayName("1. toJsonString() 方法测试")
    class ToJsonStringTests {

        @Test
        @DisplayName("null 对象应该返回 null")
        void shouldReturnNullForNullObject() {
            String result = JsonUtils.toJsonString(null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该能够序列化简单对象")
        void shouldSerializeSimpleObject() {
            Map<String, String> map = new HashMap<>();
            map.put("name", "test");

            String json = JsonUtils.toJsonString(map);

            assertThat(json).isNotNull();
            assertThat(json).contains("name");
            assertThat(json).contains("test");
        }
    }

    @Nested
    @DisplayName("2. parseObject(String, Class) 方法测试")
    class ParseObjectStringTests {

        @Test
        @DisplayName("空字符串应该返回 null")
        void shouldReturnNullForEmptyString() {
            String result = JsonUtils.parseObject("", String.class);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该能够反序列化简单对象")
        void shouldDeserializeSimpleObject() {
            String json = "{\"name\":\"test\"}";

            @SuppressWarnings("unchecked")
            Map<String, String> result = JsonUtils.parseObject(json, Map.class);

            assertThat(result).isNotNull();
            assertThat(result.get("name")).isEqualTo("test");
        }
    }

    @Nested
    @DisplayName("3. parseArray() 方法测试")
    class ParseArrayTests {

        @Test
        @DisplayName("空字符串应该返回空列表")
        void shouldReturnEmptyListForEmptyString() {
            List<String> result = JsonUtils.parseArray("", String.class);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该能够解析字符串数组")
        void shouldParseStringArray() {
            String json = "[\"apple\",\"banana\",\"cherry\"]";

            List<String> result = JsonUtils.parseArray(json, String.class);

            assertThat(result).hasSize(3);
            assertThat(result).contains("apple", "banana", "cherry");
        }
    }

    @Nested
    @DisplayName("4. parseMap() 方法测试")
    class ParseMapTests {

        @Test
        @DisplayName("空白字符串应该返回 null")
        void shouldReturnNullForBlankString() {
            Dict result = JsonUtils.parseMap("   ");
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该能够解析JSON为Dict")
        void shouldParseJsonToDict() {
            String json = "{\"key\":\"value\",\"number\":123}";

            Dict result = JsonUtils.parseMap(json);

            assertThat(result).isNotNull();
            assertThat(result.get("key")).isEqualTo("value");
            assertThat(result.get("number")).isEqualTo(123);
        }
    }

    @Nested
    @DisplayName("5. getObjectMapper() 方法测试")
    class GetObjectMapperTests {

        @Test
        @DisplayName("应该能够获取 ObjectMapper 实例")
        void shouldGetObjectMapper() {
            ObjectMapper result = JsonUtils.getObjectMapper();
            assertThat(result).isNotNull();
        }
    }
}
