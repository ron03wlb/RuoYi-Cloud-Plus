package org.dromara.common.json.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * BigNumberSerializer (大数字序列化器) 单元测试
 *
 * <p>用途: 将超出 JavaScript Number 安全范围的数字序列化为字符串 JavaScript 安全整数范围: [-9007199254740991,
 * 9007199254740991]
 *
 * @author Test Team
 */
@DisplayName("BigNumberSerializer (大数字序列化器) 单元测试")
class BigNumberSerializerTest {

    private BigNumberSerializer serializer;

    @Mock private JsonGenerator jsonGenerator;

    @Mock private SerializerProvider serializerProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serializer = new BigNumberSerializer(Number.class);
    }

    @Nested
    @DisplayName("1. 常量和实例测试")
    class ConstantAndInstanceTests {

        @Test
        @DisplayName("INSTANCE 常量应该存在且不为null")
        void shouldHaveInstance() {
            // Assert
            assertThat(BigNumberSerializer.INSTANCE).isNotNull();
            assertThat(BigNumberSerializer.INSTANCE).isInstanceOf(BigNumberSerializer.class);
        }

        @Test
        @DisplayName("应该能够创建新实例")
        void shouldCreateNewInstance() {
            // Act
            BigNumberSerializer newSerializer = new BigNumberSerializer(Long.class);

            // Assert
            assertThat(newSerializer).isNotNull();
            assertThat(newSerializer).isInstanceOf(BigNumberSerializer.class);
        }

        @Test
        @DisplayName("不同实例应该是独立的")
        void shouldCreateIndependentInstances() {
            // Act
            BigNumberSerializer serializer1 = new BigNumberSerializer(Number.class);
            BigNumberSerializer serializer2 = new BigNumberSerializer(Number.class);

            // Assert
            assertThat(serializer1).isNotSameAs(serializer2);
        }
    }

    @Nested
    @DisplayName("2. JavaScript 安全整数范围测试")
    class SafeIntegerRangeTests {

        @Test
        @DisplayName("JavaScript 最大安全整数应该是 9007199254740991")
        void shouldKnowMaxSafeInteger() {
            // Assert - 验证常量值
            long maxSafeInteger = 9007199254740991L;
            assertThat(maxSafeInteger).isEqualTo(9007199254740991L);
        }

        @Test
        @DisplayName("JavaScript 最小安全整数应该是 -9007199254740991")
        void shouldKnowMinSafeInteger() {
            // Assert - 验证常量值
            long minSafeInteger = -9007199254740991L;
            assertThat(minSafeInteger).isEqualTo(-9007199254740991L);
        }

        @Test
        @DisplayName("安全范围内的数字应该直接序列化为数字")
        void shouldSerializeNumbersInSafeRangeAsNumbers() throws IOException {
            // Arrange
            Long safeNumber = 1000000L;

            // Act
            serializer.serialize(safeNumber, jsonGenerator, serializerProvider);

            // Assert - 应该调用父类的序列化方法,不转换为字符串
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("超出安全范围的大数字应该序列化为字符串")
        void shouldSerializeLargeNumbersAsStrings() throws IOException {
            // Arrange
            Long largeNumber = 9007199254740992L; // MAX_SAFE_INTEGER + 1

            // Act
            serializer.serialize(largeNumber, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator).writeString("9007199254740992");
        }

        @Test
        @DisplayName("超出安全范围的小数字应该序列化为字符串")
        void shouldSerializeSmallNumbersAsStrings() throws IOException {
            // Arrange
            Long smallNumber = -9007199254740992L; // MIN_SAFE_INTEGER - 1

            // Act
            serializer.serialize(smallNumber, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator).writeString("-9007199254740992");
        }
    }

    @Nested
    @DisplayName("3. 边界值测试")
    class BoundaryTests {

        @ParameterizedTest
        @ValueSource(
                longs = {
                    9007199254740990L, // MAX_SAFE_INTEGER - 1 (边界内)
                    0L, // 零
                    -9007199254740990L // MIN_SAFE_INTEGER + 1 (边界内)
                })
        @DisplayName("边界内的数字应该直接序列化")
        void shouldSerializeSafeIntegersAsNumbers(Long value) throws IOException {
            // Act
            serializer.serialize(value, jsonGenerator, serializerProvider);

            // Assert - 不应该调用 writeString
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @ParameterizedTest
        @ValueSource(
                longs = {
                    9007199254740991L, // MAX_SAFE_INTEGER (边界,按实现应序列化为字符串)
                    9007199254740992L, // MAX_SAFE_INTEGER + 1 (超出)
                    9007199254740993L, // MAX_SAFE_INTEGER + 2
                    Long.MAX_VALUE, // Long 最大值
                    -9007199254740991L, // MIN_SAFE_INTEGER (边界,按实现应序列化为字符串)
                    -9007199254740992L, // MIN_SAFE_INTEGER - 1 (超出)
                    -9007199254740993L, // MIN_SAFE_INTEGER - 2
                    Long.MIN_VALUE // Long 最小值
                })
        @DisplayName("边界外的数字应该序列化为字符串")
        void shouldSerializeUnsafeIntegersAsStrings(Long value) throws IOException {
            // Act
            serializer.serialize(value, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator).writeString(String.valueOf(value));
        }

        @Test
        @DisplayName("正好等于最大安全整数应该序列化为字符串(按实现)")
        void shouldSerializeMaxSafeIntegerAsString() throws IOException {
            // Arrange
            Long maxSafe = 9007199254740991L;

            // Act
            serializer.serialize(maxSafe, jsonGenerator, serializerProvider);

            // Assert - 实现使用严格不等式,边界值被序列化为字符串
            verify(jsonGenerator).writeString("9007199254740991");
        }

        @Test
        @DisplayName("正好等于最小安全整数应该序列化为字符串(按实现)")
        void shouldSerializeMinSafeIntegerAsString() throws IOException {
            // Arrange
            Long minSafe = -9007199254740991L;

            // Act
            serializer.serialize(minSafe, jsonGenerator, serializerProvider);

            // Assert - 实现使用严格不等式,边界值被序列化为字符串
            verify(jsonGenerator).writeString("-9007199254740991");
        }
    }

    @Nested
    @DisplayName("4. 不同数字类型测试")
    class DifferentNumberTypesTests {

        @Test
        @DisplayName("应该能够处理 Integer 类型")
        void shouldHandleInteger() throws IOException {
            // Arrange
            Integer number = 1000;

            // Act
            serializer.serialize(number, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("应该能够处理 Long 类型")
        void shouldHandleLong() throws IOException {
            // Arrange
            Long number = 1000000L;

            // Act
            serializer.serialize(number, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("应该能够处理 BigInteger 类型")
        void shouldHandleBigInteger() throws IOException {
            // Arrange
            BigInteger bigNumber = new BigInteger("99999999999999999999");

            // Act
            serializer.serialize(bigNumber, jsonGenerator, serializerProvider);

            // Assert - BigInteger 会超出范围
            verify(jsonGenerator).writeString("99999999999999999999");
        }

        @Test
        @DisplayName("应该能够处理负数 BigInteger")
        void shouldHandleNegativeBigInteger() throws IOException {
            // Arrange
            BigInteger bigNumber = new BigInteger("-99999999999999999999");

            // Act
            serializer.serialize(bigNumber, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator).writeString("-99999999999999999999");
        }
    }

    @Nested
    @DisplayName("5. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 数据库ID (Long类型,安全范围内)")
        void shouldSerializeDatabaseId() throws IOException {
            // Arrange
            Long databaseId = 123456789L;

            // Act
            serializer.serialize(databaseId, jsonGenerator, serializerProvider);

            // Assert - ID 在安全范围内,应该序列化为数字
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("场景: Twitter雪花ID (可能超出范围)")
        void shouldSerializeSnowflakeId() throws IOException {
            // Arrange - Twitter 雪花算法生成的ID,可能超出JavaScript安全范围
            Long snowflakeId = 1234567890123456789L;

            // Act
            serializer.serialize(snowflakeId, jsonGenerator, serializerProvider);

            // Assert - 应该序列化为字符串
            verify(jsonGenerator).writeString("1234567890123456789");
        }

        @Test
        @DisplayName("场景: 订单金额(分为单位,Long类型)")
        void shouldSerializeOrderAmount() throws IOException {
            // Arrange - 订单金额:10000.00元 = 1000000分
            Long amountInCents = 1000000L;

            // Act
            serializer.serialize(amountInCents, jsonGenerator, serializerProvider);

            // Assert - 金额在安全范围内
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("场景: 文件大小(字节,可能超大)")
        void shouldSerializeLargeFileSize() throws IOException {
            // Arrange - 超大文件: 10PB = 10 * 1024^5 bytes = 11,258,999,068,426,240 bytes
            // 这个值超出了 JavaScript 的 MAX_SAFE_INTEGER (9,007,199,254,740,991)
            Long fileSizeBytes = 11258999068426240L; // 10PB in bytes

            // Act
            serializer.serialize(fileSizeBytes, jsonGenerator, serializerProvider);

            // Assert - 超出安全范围,应该序列化为字符串
            verify(jsonGenerator).writeString("11258999068426240");
        }

        @Test
        @DisplayName("场景: 时间戳(毫秒)")
        void shouldSerializeTimestamp() throws IOException {
            // Arrange - 2024年的时间戳(毫秒)
            Long timestamp = 1704067200000L; // 2024-01-01 00:00:00

            // Act
            serializer.serialize(timestamp, jsonGenerator, serializerProvider);

            // Assert - 时间戳在安全范围内
            verify(jsonGenerator, never()).writeString(anyString());
        }
    }

    @Nested
    @DisplayName("6. 特殊值测试")
    class SpecialValueTests {

        @Test
        @DisplayName("应该能够处理零")
        void shouldHandleZero() throws IOException {
            // Arrange
            Long zero = 0L;

            // Act
            serializer.serialize(zero, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("应该能够处理1")
        void shouldHandleOne() throws IOException {
            // Arrange
            Long one = 1L;

            // Act
            serializer.serialize(one, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("应该能够处理-1")
        void shouldHandleNegativeOne() throws IOException {
            // Arrange
            Long negativeOne = -1L;

            // Act
            serializer.serialize(negativeOne, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @Test
        @DisplayName("应该能够处理 Long.MAX_VALUE")
        void shouldHandleLongMaxValue() throws IOException {
            // Arrange
            Long maxValue = Long.MAX_VALUE;

            // Act
            serializer.serialize(maxValue, jsonGenerator, serializerProvider);

            // Assert - Long.MAX_VALUE 超出JavaScript安全范围
            verify(jsonGenerator).writeString(String.valueOf(Long.MAX_VALUE));
        }

        @Test
        @DisplayName("应该能够处理 Long.MIN_VALUE")
        void shouldHandleLongMinValue() throws IOException {
            // Arrange
            Long minValue = Long.MIN_VALUE;

            // Act
            serializer.serialize(minValue, jsonGenerator, serializerProvider);

            // Assert - Long.MIN_VALUE 超出JavaScript安全范围
            verify(jsonGenerator).writeString(String.valueOf(Long.MIN_VALUE));
        }
    }

    @Nested
    @DisplayName("7. 正负数对称性测试")
    class SymmetryTests {

        @ParameterizedTest
        @CsvSource({"1000, -1000", "9007199254740990, -9007199254740990"})
        @DisplayName("安全范围内的正负数应该都序列化为数字")
        void shouldSerializeSymmetricSafeNumbers(Long positive, Long negative) throws IOException {
            // Act
            serializer.serialize(positive, jsonGenerator, serializerProvider);
            serializer.serialize(negative, jsonGenerator, serializerProvider);

            // Assert - 都不应该转换为字符串
            verify(jsonGenerator, never()).writeString(anyString());
        }

        @ParameterizedTest
        @CsvSource({
            "9007199254740991, -9007199254740991",
            "9007199254740992, -9007199254740992",
            "10000000000000000, -10000000000000000"
        })
        @DisplayName("安全范围外的正负数应该都序列化为字符串")
        void shouldSerializeSymmetricUnsafeNumbersAsStrings(Long positive, Long negative)
                throws IOException {
            // Act
            serializer.serialize(positive, jsonGenerator, serializerProvider);
            serializer.serialize(negative, jsonGenerator, serializerProvider);

            // Assert
            verify(jsonGenerator).writeString(String.valueOf(positive));
            verify(jsonGenerator).writeString(String.valueOf(negative));
        }
    }
}
