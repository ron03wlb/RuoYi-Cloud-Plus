package org.dromara.common.json.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.dromara.common.json.handler.BigNumberSerializer;
import org.dromara.common.json.handler.CustomDateDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * JacksonConfig (Jackson配置类) 单元测试
 * <p>
 * 用途: 配置 Jackson ObjectMapper 的序列化和反序列化行为
 * - 大数字序列化为字符串
 * - LocalDateTime 格式化
 * - Date 多格式支持
 * - 时区配置
 *
 * @author Test Team
 */
@DisplayName("JacksonConfig (Jackson配置类) 单元测试")
class JacksonConfigTest {

    private JacksonConfig jacksonConfig;

    @BeforeEach
    void setUp() {
        jacksonConfig = new JacksonConfig();
    }

    @Nested
    @DisplayName("1. 实例化测试")
    class InstantiationTests {

        @Test
        @DisplayName("应该能够创建 JacksonConfig 实例")
        void shouldCreateInstance() {
            // Act
            JacksonConfig config = new JacksonConfig();

            // Assert
            assertThat(config).isNotNull();
        }
    }

    @Nested
    @DisplayName("2. registerJavaTimeModule() Bean 测试")
    class RegisterJavaTimeModuleTests {

        @Test
        @DisplayName("应该创建 JavaTimeModule 实例")
        void shouldCreateJavaTimeModule() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();

            // Assert
            assertThat(module).isNotNull();
            assertThat(module).isInstanceOf(JavaTimeModule.class);
        }

        @Test
        @DisplayName("JavaTimeModule 应该配置了序列化器")
        void shouldConfigureSerializers() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();

            // Assert - 模块应该是 JavaTimeModule 类型
            assertThat(module).isInstanceOf(JavaTimeModule.class);

            // 创建 ObjectMapper 并注册模块进行验证
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // 验证 ObjectMapper 可以正常工作
            assertThatCode(() -> {
                objectMapper.writeValueAsString(LocalDateTime.now());
                objectMapper.writeValueAsString(Long.MAX_VALUE);
                objectMapper.writeValueAsString(new BigInteger("12345"));
                objectMapper.writeValueAsString(new BigDecimal("123.45"));
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该配置 Long 类型使用 BigNumberSerializer")
        void shouldConfigureLongSerializer() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // Assert - 验证大数字序列化为字符串
            assertThatCode(() -> {
                String json = objectMapper.writeValueAsString(Long.MAX_VALUE);
                // Long.MAX_VALUE 超出 JavaScript 安全范围,应该被序列化为字符串
                assertThat(json).isEqualTo("\"" + Long.MAX_VALUE + "\"");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该配置 BigInteger 类型使用 BigNumberSerializer")
        void shouldConfigureBigIntegerSerializer() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // Assert
            assertThatCode(() -> {
                BigInteger bigNum = new BigInteger("99999999999999999999");
                String json = objectMapper.writeValueAsString(bigNum);
                assertThat(json).isEqualTo("\"99999999999999999999\"");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该配置 BigDecimal 使用 ToStringSerializer")
        void shouldConfigureBigDecimalSerializer() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // Assert
            assertThatCode(() -> {
                BigDecimal decimal = new BigDecimal("123.456");
                String json = objectMapper.writeValueAsString(decimal);
                assertThat(json).isEqualTo("\"123.456\"");
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该配置 LocalDateTime 序列化器和反序列化器")
        void shouldConfigureLocalDateTimeSerializerAndDeserializer() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // Assert - 验证 LocalDateTime 使用指定格式
            assertThatCode(() -> {
                LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 45);
                String json = objectMapper.writeValueAsString(dateTime);
                assertThat(json).isEqualTo("\"2024-01-15 10:30:45\"");

                // 反序列化
                LocalDateTime parsed = objectMapper.readValue("\"2024-01-15 10:30:45\"", LocalDateTime.class);
                assertThat(parsed).isEqualTo(dateTime);
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该配置 Date 反序列化器")
        void shouldConfigureDateDeserializer() {
            // Act
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // Assert - 验证 Date 使用自定义反序列化器
            assertThatCode(() -> {
                Date date = objectMapper.readValue("\"2024-01-15\"", Date.class);
                assertThat(date).isNotNull();
            }).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. customizer() Bean 测试")
    class CustomizerTests {

        @Test
        @DisplayName("应该创建 Jackson2ObjectMapperBuilderCustomizer 实例")
        void shouldCreateCustomizer() {
            // Act
            Jackson2ObjectMapperBuilderCustomizer customizer = jacksonConfig.customizer();

            // Assert
            assertThat(customizer).isNotNull();
        }

        @Test
        @DisplayName("Customizer 应该设置默认时区")
        void shouldSetDefaultTimeZone() {
            // Arrange
            Jackson2ObjectMapperBuilderCustomizer customizer = jacksonConfig.customizer();
            Jackson2ObjectMapperBuilder builder = mock(Jackson2ObjectMapperBuilder.class);

            // Act
            customizer.customize(builder);

            // Assert - 验证调用了 timeZone 方法
            verify(builder).timeZone(any(TimeZone.class));
        }

        @Test
        @DisplayName("Customizer 应该能够应用到 ObjectMapperBuilder")
        void shouldApplyToObjectMapperBuilder() {
            // Arrange
            Jackson2ObjectMapperBuilderCustomizer customizer = jacksonConfig.customizer();
            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

            // Act & Assert - 应该不抛异常
            assertThatCode(() -> customizer.customize(builder))
                .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. 集成测试")
    class IntegrationTests {

        @Test
        @DisplayName("完整配置应该能够正常工作")
        void shouldWorkWithFullConfiguration() throws Exception {
            // Arrange
            Module module = jacksonConfig.registerJavaTimeModule();
            Jackson2ObjectMapperBuilderCustomizer customizer = jacksonConfig.customizer();

            Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
            customizer.customize(builder);
            builder.modules(module);  // 在 build() 之前注册 module

            ObjectMapper objectMapper = builder.build();

            // Act - 测试大数字
            String longJson = objectMapper.writeValueAsString(Long.MAX_VALUE);
            assertThat(longJson).contains("\"");

            // Act - 测试 LocalDateTime
            LocalDateTime now = LocalDateTime.now();
            String dateTimeJson = objectMapper.writeValueAsString(now);
            assertThat(dateTimeJson).isNotNull();

            // Act - 测试 BigDecimal
            BigDecimal decimal = new BigDecimal("999.99");
            String decimalJson = objectMapper.writeValueAsString(decimal);
            assertThat(decimalJson).isEqualTo("\"999.99\"");

            // Act - 测试 Date 解析
            Date date = objectMapper.readValue("\"2024-01-15\"", Date.class);
            assertThat(date).isNotNull();
        }
    }

    @Nested
    @DisplayName("5. LocalDateTime 格式化测试")
    class LocalDateTimeFormattingTests {

        @Test
        @DisplayName("LocalDateTime 应该使用 yyyy-MM-dd HH:mm:ss 格式")
        void shouldUseCorrectDateTimeFormat() {
            // Arrange
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            LocalDateTime dateTime = LocalDateTime.of(2024, 3, 15, 14, 30, 45);

            // Act
            String json = assertDoesNotThrow(() -> objectMapper.writeValueAsString(dateTime));

            // Assert
            assertThat(json).isEqualTo("\"2024-03-15 14:30:45\"");
        }

        @Test
        @DisplayName("应该能够反序列化 yyyy-MM-dd HH:mm:ss 格式的字符串")
        void shouldDeserializeDateTimeFormat() {
            // Arrange
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            String json = "\"2024-03-15 14:30:45\"";

            // Act
            LocalDateTime result = assertDoesNotThrow(() ->
                objectMapper.readValue(json, LocalDateTime.class));

            // Assert
            assertThat(result).isEqualTo(LocalDateTime.of(2024, 3, 15, 14, 30, 45));
        }
    }

    @Nested
    @DisplayName("6. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 前后端数据交互 - 大数字ID安全传输")
        void shouldHandleLargeIdSafely() {
            // Arrange - 雪花ID可能超出JavaScript安全范围
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            Long snowflakeId = 1234567890123456789L;

            // Act
            String json = assertDoesNotThrow(() -> objectMapper.writeValueAsString(snowflakeId));

            // Assert - 超出JavaScript安全范围的数字应该序列化为字符串
            assertThat(json).isEqualTo("\"1234567890123456789\"");
        }

        @Test
        @DisplayName("场景: API响应中的时间字段统一格式")
        void shouldUnifyDateTimeFormat() {
            // Arrange
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            ApiResponse response = new ApiResponse();
            response.createTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);

            // Act
            String json = assertDoesNotThrow(() -> objectMapper.writeValueAsString(response));

            // Assert - 时间应该格式化为 yyyy-MM-dd HH:mm:ss
            assertThat(json).contains("\"2024-01-15 10:30:00\"");
        }

        @Test
        @DisplayName("场景: 数据库金额字段精度保持")
        void shouldMaintainDecimalPrecision() {
            // Arrange
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            OrderInfo order = new OrderInfo();
            order.amount = new BigDecimal("10000.50");

            // Act
            String json = assertDoesNotThrow(() -> objectMapper.writeValueAsString(order));

            // Assert - BigDecimal 应该序列化为字符串以保持精度
            // ToStringSerializer 会保留 BigDecimal 的原始字符串表示
            assertThat(json).containsAnyOf("\"10000.50\"", "\"10000.5\"");
        }

        @Test
        @DisplayName("场景: 多种日期格式输入兼容")
        void shouldAcceptMultipleDateFormats() {
            // Arrange
            Module module = jacksonConfig.registerJavaTimeModule();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(module);

            // Act & Assert - CustomDateDeserializer 支持多种格式
            assertThatCode(() -> {
                Date date1 = objectMapper.readValue("\"2024-01-15\"", Date.class);
                assertThat(date1).isNotNull();

                Date date2 = objectMapper.readValue("\"2024-01-15 10:30:00\"", Date.class);
                assertThat(date2).isNotNull();

                Date date3 = objectMapper.readValue("\"2024/01/15\"", Date.class);
                assertThat(date3).isNotNull();
            }).doesNotThrowAnyException();
        }
    }

    // 测试用实体类
    static class ApiResponse {
        public Long id;
        public LocalDateTime createTime;
        public BigDecimal amount;
    }

    static class OrderInfo {
        public Long orderId;
        public BigDecimal amount;
        public LocalDateTime orderTime;
    }
}
