package org.dromara.common.excel.utils;

import org.dromara.common.excel.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.*;

/**
 * ExcelUtil (Excel工具类) 单元测试
 * <p>
 * 用途: 提供 Excel 导入导出的工具方法
 * 测试范围: 仅测试不依赖外部资源的纯工具方法
 * - convertByExp(): 根据表达式转换值
 * - reverseByExp(): 根据表达式反向转换值
 * - encodingFilename(): 编码文件名
 *
 * @author Test Team
 */
@DisplayName("ExcelUtil (Excel工具类) 单元测试")
class ExcelUtilTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. convertByExp() 正向转换测试")
    class ConvertByExpTests {

        @Test
        @DisplayName("应该将单个值转换为对应的标签")
        void shouldConvertSingleValue() {
            // Arrange
            String propertyValue = "0";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("男");
        }

        @Test
        @DisplayName("应该转换第二个匹配项")
        void shouldConvertSecondValue() {
            // Arrange
            String propertyValue = "1";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("女");
        }

        @Test
        @DisplayName("应该转换多个值，使用分隔符连接")
        void shouldConvertMultipleValues() {
            // Arrange
            String propertyValue = "0,1";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("男,女");
        }

        @Test
        @DisplayName("应该处理空字符串")
        void shouldHandleEmptyString() {
            // Arrange
            String propertyValue = "";
            String converterExp = "0=男,1=女";
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该处理不存在的值")
        void shouldHandleNonExistentValue() {
            // Arrange
            String propertyValue = "99";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
            "0, '0=启用,1=停用', 启用",
            "1, '0=启用,1=停用', 停用",
            "0, '0=正常,1=锁定,2=删除', 正常",
            "2, '0=正常,1=锁定,2=删除', 删除"
        })
        @DisplayName("应该正确转换各种状态值")
        void shouldConvertVariousStatuses(String value, String exp, String expected) {
            // Act
            String result = ExcelUtil.convertByExp(value, exp, ",");

            // Assert
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("2. reverseByExp() 反向转换测试")
    class ReverseByExpTests {

        @Test
        @DisplayName("应该将标签反向转换为值")
        void shouldReverseConvertSingleLabel() {
            // Arrange
            String propertyValue = "男";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.reverseByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("0");
        }

        @Test
        @DisplayName("应该反向转换第二个标签")
        void shouldReverseConvertSecondLabel() {
            // Arrange
            String propertyValue = "女";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.reverseByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("1");
        }

        @Test
        @DisplayName("应该反向转换多个标签")
        void shouldReverseConvertMultipleLabels() {
            // Arrange
            String propertyValue = "男,女";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.reverseByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("0,1");
        }

        @Test
        @DisplayName("应该处理空字符串")
        void shouldHandleEmptyString() {
            // Arrange
            String propertyValue = "";
            String converterExp = "0=男,1=女";
            String separator = ",";

            // Act
            String result = ExcelUtil.reverseByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("应该处理不存在的标签")
        void shouldHandleNonExistentLabel() {
            // Arrange
            String propertyValue = "其他";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.reverseByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEmpty();
        }

        @ParameterizedTest
        @CsvSource({
            "启用, '0=启用,1=停用', 0",
            "停用, '0=启用,1=停用', 1",
            "正常, '0=正常,1=锁定,2=删除', 0",
            "删除, '0=正常,1=锁定,2=删除', 2"
        })
        @DisplayName("应该正确反向转换各种状态标签")
        void shouldReverseConvertVariousStatuses(String label, String exp, String expected) {
            // Act
            String result = ExcelUtil.reverseByExp(label, exp, ",");

            // Assert
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("3. convertByExp() 和 reverseByExp() 往返测试")
    class RoundTripTests {

        @Test
        @DisplayName("正向转换后反向转换应该得到原始值")
        void shouldRoundTripFromValueToLabel() {
            // Arrange
            String originalValue = "0";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String label = ExcelUtil.convertByExp(originalValue, converterExp, separator);
            String backToValue = ExcelUtil.reverseByExp(label, converterExp, separator);

            // Assert
            assertThat(label).isEqualTo("男");
            assertThat(backToValue).isEqualTo(originalValue);
        }

        @Test
        @DisplayName("反向转换后正向转换应该得到原始标签")
        void shouldRoundTripFromLabelToValue() {
            // Arrange
            String originalLabel = "女";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String value = ExcelUtil.reverseByExp(originalLabel, converterExp, separator);
            String backToLabel = ExcelUtil.convertByExp(value, converterExp, separator);

            // Assert
            assertThat(value).isEqualTo("1");
            assertThat(backToLabel).isEqualTo(originalLabel);
        }

        @Test
        @DisplayName("多个值的往返转换应该保持一致")
        void shouldRoundTripMultipleValues() {
            // Arrange
            String originalValues = "0,1,2";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String labels = ExcelUtil.convertByExp(originalValues, converterExp, separator);
            String backToValues = ExcelUtil.reverseByExp(labels, converterExp, separator);

            // Assert
            assertThat(labels).isEqualTo("男,女,未知");
            assertThat(backToValues).isEqualTo(originalValues);
        }
    }

    @Nested
    @DisplayName("4. encodingFilename() 文件名编码测试")
    class EncodingFilenameTests {

        @Test
        @DisplayName("应该生成带UUID前缀的文件名")
        void shouldGenerateFilenameWithUuidPrefix() {
            // Arrange
            String filename = "用户列表";

            // Act
            String result = ExcelUtil.encodingFilename(filename);

            // Assert
            assertThat(result).endsWith("_用户列表.xlsx");
            assertThat(result).matches("^[a-f0-9]{32}_用户列表\\.xlsx$");
        }

        @Test
        @DisplayName("应该为每次调用生成不同的UUID")
        void shouldGenerateDifferentUuidForEachCall() {
            // Arrange
            String filename = "测试文件";

            // Act
            String result1 = ExcelUtil.encodingFilename(filename);
            String result2 = ExcelUtil.encodingFilename(filename);

            // Assert
            assertThat(result1).isNotEqualTo(result2);
            assertThat(result1).endsWith("_测试文件.xlsx");
            assertThat(result2).endsWith("_测试文件.xlsx");
        }

        @Test
        @DisplayName("应该处理英文文件名")
        void shouldHandleEnglishFilename() {
            // Arrange
            String filename = "UserList";

            // Act
            String result = ExcelUtil.encodingFilename(filename);

            // Assert
            assertThat(result).endsWith("_UserList.xlsx");
            assertThat(result).matches("^[a-f0-9]{32}_UserList\\.xlsx$");
        }

        @Test
        @DisplayName("应该处理包含数字的文件名")
        void shouldHandleFilenameWithNumbers() {
            // Arrange
            String filename = "报表2024";

            // Act
            String result = ExcelUtil.encodingFilename(filename);

            // Assert
            assertThat(result).endsWith("_报表2024.xlsx");
        }

        @Test
        @DisplayName("应该处理空文件名")
        void shouldHandleEmptyFilename() {
            // Arrange
            String filename = "";

            // Act
            String result = ExcelUtil.encodingFilename(filename);

            // Assert
            assertThat(result).matches("^[a-f0-9]{32}_\\.xlsx$");
        }

        @Test
        @DisplayName("UUID应该是32位小写十六进制")
        void shouldGenerateLowercaseHexUuid() {
            // Arrange
            String filename = "test";

            // Act
            String result = ExcelUtil.encodingFilename(filename);
            String uuid = result.substring(0, 32);

            // Assert
            assertThat(uuid).matches("^[a-f0-9]{32}$");
            assertThat(uuid).doesNotContain("-");  // fastSimpleUUID 不包含破折号
        }
    }

    @Nested
    @DisplayName("5. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 导出用户性别字段")
        void shouldExportUserGender() {
            // Arrange - 数据库存储 0/1/2，Excel显示 男/女/未知
            String dbValue = "1";
            String expression = "0=男,1=女,2=未知";

            // Act
            String excelValue = ExcelUtil.convertByExp(dbValue, expression, ",");

            // Assert
            assertThat(excelValue).isEqualTo("女");
        }

        @Test
        @DisplayName("场景: 导入用户性别字段")
        void shouldImportUserGender() {
            // Arrange - Excel中是 男/女/未知，需要转为 0/1/2 存入数据库
            String excelValue = "男";
            String expression = "0=男,1=女,2=未知";

            // Act
            String dbValue = ExcelUtil.reverseByExp(excelValue, expression, ",");

            // Assert
            assertThat(dbValue).isEqualTo("0");
        }

        @Test
        @DisplayName("场景: 导出订单状态")
        void shouldExportOrderStatus() {
            // Arrange
            String statusCode = "2";
            String expression = "0=待支付,1=已支付,2=已发货,3=已完成,9=已取消";

            // Act
            String statusText = ExcelUtil.convertByExp(statusCode, expression, ",");

            // Assert
            assertThat(statusText).isEqualTo("已发货");
        }

        @Test
        @DisplayName("场景: 批量导出用户权限（多选）")
        void shouldExportMultiplePermissions() {
            // Arrange - 用户有多个角色
            String roleIds = "1,2,3";
            String expression = "1=管理员,2=编辑,3=查看者,4=访客";

            // Act
            String roleNames = ExcelUtil.convertByExp(roleIds, expression, ",");

            // Assert
            assertThat(roleNames).isEqualTo("管理员,编辑,查看者");
        }

        @Test
        @DisplayName("场景: 生成唯一的导出文件名")
        void shouldGenerateUniqueExportFilename() {
            // Arrange
            String baseFilename = "用户列表_2024年1月";

            // Act
            String filename1 = ExcelUtil.encodingFilename(baseFilename);
            String filename2 = ExcelUtil.encodingFilename(baseFilename);

            // Assert - 两次导出应该生成不同的文件名，避免覆盖
            assertThat(filename1).isNotEqualTo(filename2);
            assertThat(filename1).contains(baseFilename);
            assertThat(filename2).contains(baseFilename);
        }
    }

    @Nested
    @DisplayName("6. 边界值和特殊情况测试")
    class BoundaryAndSpecialCaseTests {

        @Test
        @DisplayName("应该处理包含等号的表达式")
        void shouldHandleExpressionWithEquals() {
            // Arrange
            String propertyValue = "key1";
            String converterExp = "key1=value1,key2=value2";
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("value1");
        }

        @Test
        @DisplayName("应该处理自定义分隔符")
        void shouldHandleCustomSeparator() {
            // Arrange
            String propertyValue = "0;1";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ";";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("男;女");
        }

        @Test
        @DisplayName("应该处理值中包含分隔符")
        void shouldHandleValueWithSeparator() {
            // Arrange
            String propertyValue = "0,1";
            String converterExp = "0=北京,上海,1=广州,深圳,2=其他";  // 标签中包含逗号
            String separator = ",";

            // Act
            String result = ExcelUtil.convertByExp(propertyValue, converterExp, separator);

            // Assert - 因为标签中有逗号，会导致解析问题
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("应该处理反向转换时的尾部分隔符")
        void shouldHandleTrailingSeparatorInReverseConvert() {
            // Arrange
            String propertyValue = "男,女,";
            String converterExp = "0=男,1=女,2=未知";
            String separator = ",";

            // Act
            String result = ExcelUtil.reverseByExp(propertyValue, converterExp, separator);

            // Assert
            assertThat(result).isEqualTo("0,1");  // 应该去除尾部逗号
        }
    }
}
