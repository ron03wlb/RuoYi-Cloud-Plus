package org.dromara.common.excel.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.dromara.common.excel.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * DefaultExcelResult (Excel导入结果) 单元测试
 *
 * <p>用途: 封装 Excel 导入的结果数据和错误信息 核心功能: 数据列表管理、错误列表管理、导入分析报告生成
 *
 * @author Test Team
 */
@DisplayName("DefaultExcelResult (Excel导入结果) 单元测试")
class DefaultExcelResultTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造应该创建空列表")
        void shouldCreateEmptyListsWithNoArgsConstructor() {
            // Act
            DefaultExcelResult<String> result = new DefaultExcelResult<>();

            // Assert
            assertThat(result.getList()).isNotNull().isEmpty();
            assertThat(result.getErrorList()).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("带参构造应该设置列表")
        void shouldSetListsWithParameterizedConstructor() {
            // Arrange
            List<String> dataList = Arrays.asList("data1", "data2");
            List<String> errorList = Arrays.asList("error1", "error2");

            // Act
            DefaultExcelResult<String> result = new DefaultExcelResult<>(dataList, errorList);

            // Assert
            assertThat(result.getList()).isEqualTo(dataList);
            assertThat(result.getErrorList()).isEqualTo(errorList);
        }

        @Test
        @DisplayName("拷贝构造应该复制 ExcelResult 的数据")
        void shouldCopyDataFromExcelResult() {
            // Arrange
            List<String> dataList = Arrays.asList("data1", "data2");
            List<String> errorList = Arrays.asList("error1");
            DefaultExcelResult<String> original = new DefaultExcelResult<>(dataList, errorList);

            // Act
            DefaultExcelResult<String> copy = new DefaultExcelResult<>(original);

            // Assert
            assertThat(copy.getList()).isEqualTo(original.getList());
            assertThat(copy.getErrorList()).isEqualTo(original.getErrorList());
        }
    }

    @Nested
    @DisplayName("2. Getter/Setter 测试")
    class GetterSetterTests {

        @Test
        @DisplayName("应该能够设置和获取数据列表")
        void shouldSetAndGetList() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            List<String> dataList = Arrays.asList("item1", "item2", "item3");

            // Act
            result.setList(dataList);

            // Assert
            assertThat(result.getList()).isEqualTo(dataList);
        }

        @Test
        @DisplayName("应该能够设置和获取错误列表")
        void shouldSetAndGetErrorList() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            List<String> errorList = Arrays.asList("错误1", "错误2");

            // Act
            result.setErrorList(errorList);

            // Assert
            assertThat(result.getErrorList()).isEqualTo(errorList);
        }
    }

    @Nested
    @DisplayName("3. getAnalysis() 分析报告测试")
    class AnalysisTests {

        @Test
        @DisplayName("没有数据时应该返回失败提示")
        void shouldReturnFailureMessageWhenNoData() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(analysis).isEqualTo("读取失败，未解析到数据");
        }

        @Test
        @DisplayName("有数据且无错误时应该返回成功提示")
        void shouldReturnSuccessMessageWhenNoErrors() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            List<String> dataList = Arrays.asList("data1", "data2", "data3");
            result.setList(dataList);

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共3条");
        }

        @Test
        @DisplayName("有数据且有错误时应该返回空字符串")
        void shouldReturnEmptyStringWhenHasErrors() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            List<String> dataList = Arrays.asList("data1", "data2");
            List<String> errorList = Arrays.asList("错误1");
            result.setList(dataList);
            result.setErrorList(errorList);

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(analysis).isEmpty();
        }

        @Test
        @DisplayName("单条数据成功时应该返回正确的数量")
        void shouldReturnCorrectCountForSingleSuccess() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            result.setList(Arrays.asList("data1"));

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共1条");
        }

        @Test
        @DisplayName("多条数据成功时应该返回正确的数量")
        void shouldReturnCorrectCountForMultipleSuccess() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            result.setList(Arrays.asList("d1", "d2", "d3", "d4", "d5"));

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共5条");
        }
    }

    @Nested
    @DisplayName("4. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 成功导入100条用户数据")
        void shouldHandleSuccessfulImportOf100Users() {
            // Arrange
            DefaultExcelResult<UserData> result = new DefaultExcelResult<>();
            List<UserData> users = new ArrayList<>();
            for (int i = 1; i <= 100; i++) {
                users.add(new UserData("user" + i, "user" + i + "@example.com"));
            }
            result.setList(users);

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).hasSize(100);
            assertThat(result.getErrorList()).isEmpty();
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共100条");
        }

        @Test
        @DisplayName("场景: 导入失败，Excel文件为空")
        void shouldHandleEmptyExcelFile() {
            // Arrange
            DefaultExcelResult<UserData> result = new DefaultExcelResult<>();

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).isEmpty();
            assertThat(result.getErrorList()).isEmpty();
            assertThat(analysis).isEqualTo("读取失败，未解析到数据");
        }

        @Test
        @DisplayName("场景: 部分导入成功，部分失败")
        void shouldHandlePartialImportSuccess() {
            // Arrange
            DefaultExcelResult<UserData> result = new DefaultExcelResult<>();
            result.setList(
                    Arrays.asList(
                            new UserData("user1", "user1@example.com"),
                            new UserData("user2", "user2@example.com")));
            result.setErrorList(Arrays.asList("第3行: 邮箱格式错误", "第5行: 用户名不能为空"));

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).hasSize(2);
            assertThat(result.getErrorList()).hasSize(2);
            assertThat(analysis).isEmpty(); // 有错误时返回空字符串
        }

        @Test
        @DisplayName("场景: 导入10000条数据验证性能")
        void shouldHandleLargeDataSet() {
            // Arrange
            DefaultExcelResult<UserData> result = new DefaultExcelResult<>();
            List<UserData> largeDataSet = new ArrayList<>();
            for (int i = 1; i <= 10000; i++) {
                largeDataSet.add(new UserData("user" + i, "user" + i + "@example.com"));
            }
            result.setList(largeDataSet);

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).hasSize(10000);
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共10000条");
        }
    }

    @Nested
    @DisplayName("5. 边界值和特殊情况测试")
    class BoundaryAndSpecialCaseTests {

        @Test
        @DisplayName("应该处理 null 的数据列表")
        void shouldHandleNullDataList() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            result.setList(null);

            // Act & Assert
            assertThatThrownBy(() -> result.getAnalysis()).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("应该处理 null 的错误列表")
        void shouldHandleNullErrorList() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            result.setList(Arrays.asList("data1"));
            result.setErrorList(null);

            // Act & Assert
            assertThatThrownBy(() -> result.getAnalysis()).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("应该正确计算只有错误无数据的情况")
        void shouldHandleOnlyErrors() {
            // Arrange
            DefaultExcelResult<String> result = new DefaultExcelResult<>();
            result.setErrorList(Arrays.asList("错误1", "错误2", "错误3"));

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).isEmpty();
            assertThat(result.getErrorList()).hasSize(3);
            assertThat(analysis).isEqualTo("读取失败，未解析到数据");
        }
    }

    @Nested
    @DisplayName("6. 不同数据类型测试")
    class DifferentDataTypeTests {

        @Test
        @DisplayName("应该支持 Integer 类型")
        void shouldSupportIntegerType() {
            // Arrange
            DefaultExcelResult<Integer> result = new DefaultExcelResult<>();
            result.setList(Arrays.asList(1, 2, 3, 4, 5));

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).containsExactly(1, 2, 3, 4, 5);
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共5条");
        }

        @Test
        @DisplayName("应该支持自定义对象类型")
        void shouldSupportCustomObjectType() {
            // Arrange
            DefaultExcelResult<UserData> result = new DefaultExcelResult<>();
            List<UserData> users =
                    Arrays.asList(
                            new UserData("Alice", "alice@example.com"),
                            new UserData("Bob", "bob@example.com"));
            result.setList(users);

            // Act
            String analysis = result.getAnalysis();

            // Assert
            assertThat(result.getList()).hasSize(2);
            assertThat(result.getList().get(0).name).isEqualTo("Alice");
            assertThat(analysis).isEqualTo("恭喜您，全部读取成功！共2条");
        }
    }

    // 测试用的简单数据类
    private static class UserData {
        String name;
        String email;

        UserData(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
