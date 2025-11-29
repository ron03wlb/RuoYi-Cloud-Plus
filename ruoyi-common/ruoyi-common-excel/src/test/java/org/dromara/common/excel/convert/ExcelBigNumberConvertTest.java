package org.dromara.common.excel.convert;

import static org.assertj.core.api.Assertions.assertThat;

import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import java.math.BigDecimal;
import org.dromara.common.excel.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;

/**
 * ExcelBigNumberConvert (大数值转换器) 单元测试.
 *
 * <p>用途: 处理 Excel 中超过 15 位的大数值，防止精度丢失 核心逻辑: 超过 15 位的 Long 转为字符串，否则保持数值类型
 *
 * @author Test Team
 */
@DisplayName("ExcelBigNumberConvert (大数值转换器) 单元测试")
class ExcelBigNumberConvertTest extends BaseUnitTest {

  private ExcelBigNumberConvert converter;

  @Mock private ExcelContentProperty contentProperty;

  @Mock private GlobalConfiguration globalConfiguration;

  @BeforeEach
  void setUp() {
    converter = new ExcelBigNumberConvert();
  }

  @Nested
  @DisplayName("1. 类型支持测试")
  class TypeSupportTests {

    @Test
    @DisplayName("应该支持 Long 类型")
    void shouldSupportLongType() {
      // Act
      Class<Long> supportedType = converter.supportJavaTypeKey();

      // Assert
      assertThat(supportedType).isEqualTo(Long.class);
    }

    @Test
    @DisplayName("应该支持 STRING 类型的 Excel 单元格")
    void shouldSupportStringExcelType() {
      // Act
      CellDataTypeEnum supportedExcelType = converter.supportExcelTypeKey();

      // Assert
      assertThat(supportedExcelType).isEqualTo(CellDataTypeEnum.STRING);
    }
  }

  @Nested
  @DisplayName("2. 读取转换测试 (Excel -> Java)")
  class ConvertToJavaDataTests {

    @Test
    @DisplayName("应该能够将字符串转换为 Long")
    void shouldConvertStringToLong() {
      // Arrange
      ReadCellData<String> cellData = new ReadCellData<>();
      cellData.setData("123456789");

      // Act
      Long result = converter.convertToJavaData(cellData, contentProperty, globalConfiguration);

      // Assert
      assertThat(result).isEqualTo(123456789L);
    }

    @Test
    @DisplayName("应该能够将大数字字符串转换为 Long")
    void shouldConvertBigNumberStringToLong() {
      // Arrange
      ReadCellData<String> cellData = new ReadCellData<>();
      cellData.setData("12345678901234567890");

      // Act
      Long result = converter.convertToJavaData(cellData, contentProperty, globalConfiguration);

      // Assert
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该能够处理 0")
    void shouldConvertZero() {
      // Arrange
      ReadCellData<String> cellData = new ReadCellData<>();
      cellData.setData("0");

      // Act
      Long result = converter.convertToJavaData(cellData, contentProperty, globalConfiguration);

      // Assert
      assertThat(result).isEqualTo(0L);
    }

    @Test
    @DisplayName("应该能够处理负数")
    void shouldConvertNegativeNumber() {
      // Arrange
      ReadCellData<String> cellData = new ReadCellData<>();
      cellData.setData("-123456");

      // Act
      Long result = converter.convertToJavaData(cellData, contentProperty, globalConfiguration);

      // Assert
      assertThat(result).isEqualTo(-123456L);
    }
  }

  @Nested
  @DisplayName("3. 写入转换测试 (Java -> Excel)")
  class ConvertToExcelDataTests {

    @Nested
    @DisplayName("3.1 小于等于15位的数字")
    class SmallNumberTests {

      @Test
      @DisplayName("应该将小数字保持为 NUMBER 类型")
      void shouldKeepSmallNumberAsNumber() {
        // Arrange
        Long smallNumber = 123456789L; // 9位

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(smallNumber, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
        assertThat(result.getData()).isInstanceOf(BigDecimal.class);
        assertThat(((BigDecimal) result.getData()).longValue()).isEqualTo(123456789L);
      }

      @Test
      @DisplayName("应该将15位数字保持为 NUMBER 类型")
      void shouldKeep15DigitNumberAsNumber() {
        // Arrange
        Long fifteenDigits = 123456789012345L; // 正好15位

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(fifteenDigits, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
        assertThat(result.getData()).isInstanceOf(BigDecimal.class);
      }

      @Test
      @DisplayName("应该将0保持为 NUMBER 类型")
      void shouldKeepZeroAsNumber() {
        // Arrange
        Long zero = 0L;

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(zero, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
        assertThat(((BigDecimal) result.getData()).longValue()).isEqualTo(0L);
      }

      @Test
      @DisplayName("应该将负数保持为 NUMBER 类型")
      void shouldKeepNegativeNumberAsNumber() {
        // Arrange
        Long negativeNumber = -123456L;

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(negativeNumber, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
      }
    }

    @Nested
    @DisplayName("3.2 大于15位的数字")
    class BigNumberTests {

      @Test
      @DisplayName("应该将16位数字转换为 STRING")
      void shouldConvert16DigitNumberToString() {
        // Arrange
        Long sixteenDigits = 1234567890123456L; // 16位

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(sixteenDigits, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getData()).isInstanceOf(String.class);
        assertThat(result.getData()).isEqualTo("1234567890123456");
      }

      @Test
      @DisplayName("应该将18位数字转换为 STRING")
      void shouldConvert18DigitNumberToString() {
        // Arrange
        Long eighteenDigits = 123456789012345678L; // 18位

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(eighteenDigits, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getData()).isInstanceOf(String.class);
        assertThat(result.getData()).isEqualTo("123456789012345678");
      }

      @Test
      @DisplayName("应该将 Long.MAX_VALUE 转换为 STRING")
      void shouldConvertMaxLongToString() {
        // Arrange
        Long maxLong = Long.MAX_VALUE; // 19位

        // Act
        WriteCellData<Object> result =
            converter.convertToExcelData(maxLong, contentProperty, globalConfiguration);

        // Assert
        assertThat(result.getData()).isInstanceOf(String.class);
        assertThat(result.getData()).isEqualTo(String.valueOf(Long.MAX_VALUE));
      }
    }
  }

  @Nested
  @DisplayName("4. 边界值测试")
  class BoundaryValueTests {

    @ParameterizedTest
    @ValueSource(
        longs = {
          1L, // 1位
          10L, // 2位
          100L, // 3位
          1000L, // 4位
          10000L, // 5位
          100000L, // 6位
          1000000L, // 7位
          10000000L, // 8位
          100000000L, // 9位
          1000000000L, // 10位
          10000000000L, // 11位
          100000000000L, // 12位
          1000000000000L, // 13位
          10000000000000L, // 14位
          100000000000000L // 15位 - 边界值
        })
    @DisplayName("15位及以下的数字应该保持为 NUMBER 类型")
    void shouldKeepNumbersUpTo15DigitsAsNumber(Long number) {
      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(number, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
      assertThat(result.getData()).isInstanceOf(BigDecimal.class);
    }

    @ParameterizedTest
    @ValueSource(
        longs = {
          1000000000000000L, // 16位 - 刚好超过边界
          10000000000000000L, // 17位
          100000000000000000L, // 18位
          1000000000000000000L, // 19位
          9223372036854775807L // Long.MAX_VALUE (19位)
        })
    @DisplayName("16位及以上的数字应该转换为 STRING")
    void shouldConvertNumbersOver15DigitsToString(Long number) {
      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(number, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getData()).isInstanceOf(String.class);
      assertThat(result.getData()).isEqualTo(String.valueOf(number));
    }
  }

  @Nested
  @DisplayName("5. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("场景: 导出用户ID (一般不超过15位)")
    void shouldExportUserIdAsNumber() {
      // Arrange - 假设系统使用雪花算法生成的ID
      Long userId = 1234567890123L; // 13位

      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(userId, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
    }

    @Test
    @DisplayName("场景: 导出身份证号 (18位数字)")
    void shouldExportIdCardNumberAsString() {
      // Arrange - 身份证号是18位数字
      Long idCardNumber = 110101199003071234L; // 18位

      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(idCardNumber, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getData()).isInstanceOf(String.class);
      assertThat(result.getData()).isEqualTo("110101199003071234");
    }

    @Test
    @DisplayName("场景: 导出手机号 (11位数字)")
    void shouldExportPhoneNumberAsNumber() {
      // Arrange
      Long phoneNumber = 13800138000L; // 11位

      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(phoneNumber, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getType()).isEqualTo(CellDataTypeEnum.NUMBER);
    }

    @Test
    @DisplayName("场景: 导入身份证号字符串")
    void shouldImportIdCardNumber() {
      // Arrange - 从Excel导入身份证号
      ReadCellData<String> cellData = new ReadCellData<>();
      cellData.setData("110101199003071234");

      // Act
      Long result = converter.convertToJavaData(cellData, contentProperty, globalConfiguration);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("6. 精度保持测试")
  class PrecisionTests {

    @Test
    @DisplayName("16位数字应该精确保存为字符串，不丢失精度")
    void shouldPreservePrecisionFor16Digits() {
      // Arrange
      Long originalNumber = 1234567890123456L;

      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(originalNumber, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getData()).isEqualTo("1234567890123456");
      // 验证转回Long后值不变
      assertThat(Long.parseLong((String) result.getData())).isEqualTo(originalNumber);
    }

    @Test
    @DisplayName("18位数字应该精确保存为字符串，不丢失精度")
    void shouldPreservePrecisionFor18Digits() {
      // Arrange
      Long originalNumber = 123456789012345678L;

      // Act
      WriteCellData<Object> result =
          converter.convertToExcelData(originalNumber, contentProperty, globalConfiguration);

      // Assert
      assertThat(result.getData()).isEqualTo("123456789012345678");
      assertThat(Long.parseLong((String) result.getData())).isEqualTo(originalNumber);
    }
  }
}
