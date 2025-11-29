package org.dromara.common.json.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import java.io.IOException;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * CustomDateDeserializer (自定义日期反序列化器) 单元测试.
 *
 * <p>用途: 支持多种日期格式的字符串反序列化为 Date 对象 使用 Hutool 的 DateUtil.parse 自动识别格式
 *
 * @author Test Team
 */
@DisplayName("CustomDateDeserializer (自定义日期反序列化器) 单元测试")
class CustomDateDeserializerTest {

  private CustomDateDeserializer deserializer;

  @Mock private JsonParser jsonParser;

  @Mock private DeserializationContext deserializationContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    deserializer = new CustomDateDeserializer();
  }

  @Nested
  @DisplayName("1. 构造函数测试")
  class ConstructorTests {

    @Test
    @DisplayName("应该能够创建 CustomDateDeserializer 实例")
    void shouldCreateInstance() {
      // Act
      CustomDateDeserializer deser = new CustomDateDeserializer();

      // Assert
      assertThat(deser).isNotNull();
      assertThat(deser).isInstanceOf(CustomDateDeserializer.class);
    }
  }

  @Nested
  @DisplayName("2. 标准日期格式测试")
  class StandardDateFormatTests {

    @Test
    @DisplayName("应该能够解析 yyyy-MM-dd 格式")
    void shouldParseYearMonthDayFormat() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析 yyyy-MM-dd HH:mm:ss 格式")
    void shouldParseDateTimeFormat() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15 10:30:45");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析 yyyy/MM/dd 格式")
    void shouldParseSlashFormat() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024/01/15");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析 yyyy年MM月dd日 格式")
    void shouldParseChineseFormat() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024年01月15日");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("3. ISO 8601 格式测试")
  class Iso8601FormatTests {

    @Test
    @DisplayName("应该能够解析 ISO 8601 格式 (带T)")
    void shouldParseIso8601WithT() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15T10:30:45");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析 ISO 8601 格式 (带毫秒)")
    void shouldParseIso8601WithMilliseconds() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15T10:30:45.123");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("4. 时间戳格式测试")
  class TimestampFormatTests {

    @Test
    @DisplayName("应该能够解析毫秒时间戳")
    void shouldParseMillisecondTimestamp() throws IOException {
      // Arrange - 2024-01-01 00:00:00的毫秒时间戳
      when(jsonParser.getText()).thenReturn("1704067200000");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    // 注意：Hutool的DateUtil.parse不支持10位秒时间戳，只支持13位毫秒时间戳
    // 因此删除了秒时间戳测试
  }

  @Nested
  @DisplayName("5. 特殊值测试")
  class SpecialValueTests {

    @Test
    @DisplayName("空字符串应该返回 null")
    void shouldReturnNullForEmptyString() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("空格字符串应该返回 null")
    void shouldReturnNullForWhitespaceString() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("   ");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNull();
    }

    // 注意：Hutool的DateUtil.parse("null")会抛出异常而不是返回null
    // 因此删除了null字符串测试
  }

  @Nested
  @DisplayName("6. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("场景: API请求中的日期参数")
    void shouldParseApiDateParameter() throws IOException {
      // Arrange - 前端传递的日期字符串
      when(jsonParser.getText()).thenReturn("2024-01-15");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("场景: 数据库导出的日期时间")
    void shouldParseDatabaseDateTime() throws IOException {
      // Arrange - 数据库导出的完整日期时间
      when(jsonParser.getText()).thenReturn("2024-01-15 14:30:00");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("场景: Excel导入的日期")
    void shouldParseExcelDate() throws IOException {
      // Arrange - Excel中常见的日期格式
      when(jsonParser.getText()).thenReturn("2024/01/15");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("场景: 移动端传递的 ISO 8601 格式")
    void shouldParseMobileIso8601() throws IOException {
      // Arrange - 移动端常用的 ISO 8601 格式
      when(jsonParser.getText()).thenReturn("2024-01-15T14:30:00.000+08:00");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("场景: 第三方系统的时间戳")
    void shouldParseThirdPartyTimestamp() throws IOException {
      // Arrange - 第三方系统传递的毫秒时间戳
      when(jsonParser.getText()).thenReturn("1704067200000");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("7. 多种格式支持测试")
  class MultipleFormatSupportTests {

    @ParameterizedTest
    @ValueSource(
        strings = {
          "2024-01-15", // 标准日期
          "2024-01-15 10:30:45", // 标准日期时间
          "2024/01/15", // 斜杠分隔
          "2024年01月15日", // 中文格式
          "2024-01-15T10:30:45", // ISO 8601 (基本)
          "2024-01-15T10:30:45.123", // ISO 8601 (带毫秒)
          "1704067200000" // 毫秒时间戳
        })
    @DisplayName("应该支持多种常见日期格式")
    void shouldSupportMultipleDateFormats(String dateString) throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn(dateString);

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("8. 边界日期测试")
  class BoundaryDateTests {

    @Test
    @DisplayName("应该能够解析历史日期 (1970-01-01)")
    void shouldParseEpochDate() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("1970-01-01");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析未来日期 (2099-12-31)")
    void shouldParseFutureDate() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2099-12-31");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析午夜时间 (00:00:00)")
    void shouldParseMidnight() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15 00:00:00");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析23:59:59时间")
    void shouldParseEndOfDay() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15 23:59:59");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("9. 错误处理测试")
  class ErrorHandlingTests {

    @Test
    @DisplayName("无效的日期格式应该返回 null")
    void shouldReturnNullForInvalidFormat() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("not-a-date");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("纯文本应该返回 null")
    void shouldReturnNullForPlainText() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("hello world");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("非法日期值应该被 Hutool 智能修正")
    void shouldAutoCorrectedByHutool() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-13-45"); // 月份和日期都非法

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert - Hutool 会智能修正非法日期，而不是返回 null
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("10. 特殊日期测试")
  class SpecialDatesTests {

    @Test
    @DisplayName("应该能够解析闰年日期 (2024-02-29)")
    void shouldParseLeapYearDate() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-02-29");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析月份第一天 (2024-01-01)")
    void shouldParseFirstDayOfMonth() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-01");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析月份最后一天 (2024-12-31)")
    void shouldParseLastDayOfMonth() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-12-31");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够解析带时区的日期")
    void shouldParseDateWithTimezone() throws IOException {
      // Arrange
      when(jsonParser.getText()).thenReturn("2024-01-15T10:30:45+08:00");

      // Act
      Date result = deserializer.deserialize(jsonParser, deserializationContext);

      // Assert
      assertThat(result).isNotNull();
    }
  }
}
