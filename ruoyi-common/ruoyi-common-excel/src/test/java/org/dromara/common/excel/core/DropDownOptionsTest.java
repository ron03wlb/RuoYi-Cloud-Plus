package org.dromara.common.excel.core;

import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.excel.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * DropDownOptions (Excel下拉选项) 单元测试
 * <p>
 * 用途: 为 Excel 导出提供级联下拉选项功能
 * 核心功能: 创建选项值、解析选项值、构建级联下拉
 *
 * @author Test Team
 */
@DisplayName("DropDownOptions (Excel下拉选项) 单元测试")
class DropDownOptionsTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造应该创建默认值")
        void shouldCreateDefaultValuesWithNoArgsConstructor() {
            // Act
            DropDownOptions options = new DropDownOptions();

            // Assert
            assertThat(options.getIndex()).isZero();
            assertThat(options.getNextIndex()).isZero();
            assertThat(options.getOptions()).isEmpty();
            assertThat(options.getNextOptions()).isEmpty();
        }

        @Test
        @DisplayName("单级下拉构造应该设置索引和选项")
        void shouldCreateSingleLevelDropDown() {
            // Arrange
            List<String> options = Arrays.asList("选项1", "选项2", "选项3");

            // Act
            DropDownOptions dropDown = new DropDownOptions(2, options);

            // Assert
            assertThat(dropDown.getIndex()).isEqualTo(2);
            assertThat(dropDown.getOptions()).isEqualTo(options);
        }

        @Test
        @DisplayName("完整构造应该设置所有字段")
        void shouldCreateFullDropDown() {
            // Arrange
            List<String> options = Arrays.asList("父1", "父2");
            Map<String, List<String>> nextOptions = new HashMap<>();
            nextOptions.put("父1", Arrays.asList("子1-1", "子1-2"));
            nextOptions.put("父2", Arrays.asList("子2-1", "子2-2"));

            // Act
            DropDownOptions dropDown = new DropDownOptions(0, 1, options, nextOptions);

            // Assert
            assertThat(dropDown.getIndex()).isZero();
            assertThat(dropDown.getNextIndex()).isEqualTo(1);
            assertThat(dropDown.getOptions()).isEqualTo(options);
            assertThat(dropDown.getNextOptions()).isEqualTo(nextOptions);
        }
    }

    @Nested
    @DisplayName("2. createOptionValue() 创建选项值测试")
    class CreateOptionValueTests {

        @Test
        @DisplayName("应该创建单个参数的选项值")
        void shouldCreateSingleParameterOption() {
            // Act
            String result = DropDownOptions.createOptionValue("北京");

            // Assert
            assertThat(result).isEqualTo("北京");
        }

        @Test
        @DisplayName("应该创建多个参数的选项值，使用下划线分隔")
        void shouldCreateMultipleParameterOption() {
            // Act
            String result = DropDownOptions.createOptionValue("北京", "101", "一线城市");

            // Assert
            assertThat(result).isEqualTo("北京_101_一线城市");
        }

        @Test
        @DisplayName("应该处理中文字符")
        void shouldHandleChineseCharacters() {
            // Act
            String result = DropDownOptions.createOptionValue("中国", "北京市", "朝阳区");

            // Assert
            assertThat(result).isEqualTo("中国_北京市_朝阳区");
        }

        @Test
        @DisplayName("应该处理英文字符")
        void shouldHandleEnglishCharacters() {
            // Act
            String result = DropDownOptions.createOptionValue("China", "Beijing", "Chaoyang");

            // Assert
            assertThat(result).isEqualTo("China_Beijing_Chaoyang");
        }

        @Test
        @DisplayName("应该处理数字")
        void shouldHandleNumbers() {
            // Act
            String result = DropDownOptions.createOptionValue("城市", "101", "200");

            // Assert
            assertThat(result).isEqualTo("城市_101_200");
        }

        @Test
        @DisplayName("应该处理混合字符")
        void shouldHandleMixedCharacters() {
            // Act
            String result = DropDownOptions.createOptionValue("Beijing2023", "区域A1", "编号001");

            // Assert
            assertThat(result).isEqualTo("Beijing2023_区域A1_编号001");
        }

        @Test
        @DisplayName("应该自动去除前后空格")
        void shouldTrimWhitespace() {
            // Act
            String result = DropDownOptions.createOptionValue("  北京  ", "  101  ");

            // Assert
            assertThat(result).isEqualTo("北京_101");
        }

        @Test
        @DisplayName("应该拒绝以纯数字开头的选项")
        void shouldRejectPureNumberStart() {
            // Act & Assert
            assertThatThrownBy(() -> DropDownOptions.createOptionValue("123"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("禁止以数字开头");
        }

        @Test
        @DisplayName("应该拒绝包含特殊符号的选项")
        void shouldRejectSpecialCharacters() {
            // Act & Assert
            assertThatThrownBy(() -> DropDownOptions.createOptionValue("北京-朝阳"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("选项数据不符合规则");

            assertThatThrownBy(() -> DropDownOptions.createOptionValue("北京@朝阳"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("选项数据不符合规则");

            assertThatThrownBy(() -> DropDownOptions.createOptionValue("北京+朝阳"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("选项数据不符合规则");
        }

        @Test
        @DisplayName("应该拒绝包含空格的选项")
        void shouldRejectSpacesInMiddle() {
            // Act & Assert
            assertThatThrownBy(() -> DropDownOptions.createOptionValue("北京 朝阳"))
                .isInstanceOf(ServiceException.class)
                .hasMessageContaining("选项数据不符合规则");
        }
    }

    @Nested
    @DisplayName("3. analyzeOptionValue() 解析选项值测试")
    class AnalyzeOptionValueTests {

        @Test
        @DisplayName("应该解析单个参数的选项值")
        void shouldParseSingleParameterOption() {
            // Arrange
            String option = "北京";

            // Act
            List<String> result = DropDownOptions.analyzeOptionValue(option);

            // Assert
            assertThat(result).containsExactly("北京");
        }

        @Test
        @DisplayName("应该解析多个参数的选项值")
        void shouldParseMultipleParameterOption() {
            // Arrange
            String option = "北京_101_一线城市";

            // Act
            List<String> result = DropDownOptions.analyzeOptionValue(option);

            // Assert
            assertThat(result).containsExactly("北京", "101", "一线城市");
        }

        @Test
        @DisplayName("应该解析包含空格的选项值（去除空格）")
        void shouldParseOptionWithSpaces() {
            // Arrange
            String option = "  北京  _  101  _  一线城市  ";

            // Act
            List<String> result = DropDownOptions.analyzeOptionValue(option);

            // Assert
            assertThat(result).containsExactly("北京", "101", "一线城市");
        }

        @Test
        @DisplayName("应该解析空字符串为空列表")
        void shouldParseEmptyStringAsEmptyList() {
            // Act
            List<String> result = DropDownOptions.analyzeOptionValue("");

            // Assert
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. createOptionValue() 和 analyzeOptionValue() 往返测试")
    class RoundTripTests {

        @Test
        @DisplayName("创建和解析应该互为逆操作（单个参数）")
        void shouldRoundTripSingleParameter() {
            // Arrange
            String original = "北京";

            // Act
            String created = DropDownOptions.createOptionValue(original);
            List<String> analyzed = DropDownOptions.analyzeOptionValue(created);

            // Assert
            assertThat(analyzed).containsExactly(original);
        }

        @Test
        @DisplayName("创建和解析应该互为逆操作（多个参数）")
        void shouldRoundTripMultipleParameters() {
            // Arrange
            String param1 = "北京";
            String param2 = "101";
            String param3 = "一线城市";

            // Act
            String created = DropDownOptions.createOptionValue(param1, param2, param3);
            List<String> analyzed = DropDownOptions.analyzeOptionValue(created);

            // Assert
            assertThat(analyzed).containsExactly(param1, param2, param3);
        }

        @Test
        @DisplayName("创建和解析应该互为逆操作（中英文数字混合）")
        void shouldRoundTripMixedContent() {
            // Arrange
            String[] params = {"Beijing2023", "区域A1", "Code999", "中文123"};

            // Act
            String created = DropDownOptions.createOptionValue((Object[]) params);
            List<String> analyzed = DropDownOptions.analyzeOptionValue(created);

            // Assert
            assertThat(analyzed).containsExactly(params);
        }
    }

    @Nested
    @DisplayName("5. buildLinkedOptions() 构建级联下拉测试")
    class BuildLinkedOptionsTests {

        @Test
        @DisplayName("应该构建省市级联下拉")
        void shouldBuildProvinceCityLinkedOptions() {
            // Arrange
            List<Region> provinces = Arrays.asList(
                new Region(1, 0, "北京市"),
                new Region(2, 0, "上海市")
            );
            List<Region> cities = Arrays.asList(
                new Region(11, 1, "朝阳区"),
                new Region(12, 1, "海淀区"),
                new Region(21, 2, "浦东新区"),
                new Region(22, 2, "黄浦区")
            );

            // Act
            DropDownOptions result = DropDownOptions.buildLinkedOptions(
                provinces,
                0,  // 父下拉在第0列
                cities,
                1,  // 子下拉在第1列
                Region::getId,
                Region::getParentId,
                region -> DropDownOptions.createOptionValue(region.getName(), String.valueOf(region.getId()))
            );

            // Assert
            assertThat(result.getIndex()).isZero();
            assertThat(result.getNextIndex()).isEqualTo(1);
            assertThat(result.getOptions()).hasSize(2);
            assertThat(result.getNextOptions()).hasSize(2);

            // 验证父选项
            assertThat(result.getOptions()).contains("北京市_1", "上海市_2");

            // 验证子选项
            assertThat(result.getNextOptions().get("北京市_1"))
                .contains("朝阳区_11", "海淀区_12");
            assertThat(result.getNextOptions().get("上海市_2"))
                .contains("浦东新区_21", "黄浦区_22");
        }

        @Test
        @DisplayName("应该处理没有子选项的父选项")
        void shouldHandleParentWithoutChildren() {
            // Arrange
            List<Region> provinces = Arrays.asList(
                new Region(1, 0, "北京市"),
                new Region(2, 0, "上海市"),
                new Region(3, 0, "深圳市")  // 没有子区域
            );
            List<Region> cities = Arrays.asList(
                new Region(11, 1, "朝阳区"),
                new Region(21, 2, "浦东新区")
            );

            // Act
            DropDownOptions result = DropDownOptions.buildLinkedOptions(
                provinces, 0, cities, 1,
                Region::getId, Region::getParentId,
                region -> region.getName()
            );

            // Assert
            assertThat(result.getOptions()).hasSize(3);
            assertThat(result.getNextOptions()).hasSize(2);  // 只有2个父选项有子选项
            assertThat(result.getNextOptions()).doesNotContainKey("深圳市");
        }

        @Test
        @DisplayName("应该处理空的子列表")
        void shouldHandleEmptyChildList() {
            // Arrange
            List<Region> provinces = Arrays.asList(
                new Region(1, 0, "北京市")
            );
            List<Region> cities = Arrays.asList();  // 空列表

            // Act
            DropDownOptions result = DropDownOptions.buildLinkedOptions(
                provinces, 0, cities, 1,
                Region::getId, Region::getParentId,
                region -> region.getName()
            );

            // Assert
            assertThat(result.getOptions()).hasSize(1);
            assertThat(result.getNextOptions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景: 导出用户列表，性别列有下拉选项")
        void shouldCreateGenderDropDown() {
            // Arrange
            List<String> genderOptions = Arrays.asList(
                DropDownOptions.createOptionValue("男", "1"),
                DropDownOptions.createOptionValue("女", "2"),
                DropDownOptions.createOptionValue("未知", "0")
            );

            // Act
            DropDownOptions dropDown = new DropDownOptions(2, genderOptions);

            // Assert
            assertThat(dropDown.getIndex()).isEqualTo(2);
            assertThat(dropDown.getOptions()).containsExactly("男_1", "女_2", "未知_0");
        }

        @Test
        @DisplayName("场景: 导出订单列表，状态列有下拉选项")
        void shouldCreateOrderStatusDropDown() {
            // Arrange
            List<String> statusOptions = Arrays.asList(
                DropDownOptions.createOptionValue("待支付", "0"),
                DropDownOptions.createOptionValue("已支付", "1"),
                DropDownOptions.createOptionValue("已发货", "2"),
                DropDownOptions.createOptionValue("已完成", "3"),
                DropDownOptions.createOptionValue("已取消", "9")
            );

            // Act
            DropDownOptions dropDown = new DropDownOptions(5, statusOptions);

            // Assert
            assertThat(dropDown.getIndex()).isEqualTo(5);
            assertThat(dropDown.getOptions()).hasSize(5);
        }

        @Test
        @DisplayName("场景: 导出三级地区级联下拉（省-市）")
        void shouldCreateProvinceDistrictCascade() {
            // Arrange - 使用Region类演示三级联动：省份和下属的市区
            List<Region> provinces = Arrays.asList(
                new Region(1, 0, "北京市"),
                new Region(2, 0, "上海市")
            );
            List<Region> districts = Arrays.asList(
                new Region(11, 1, "朝阳区"),
                new Region(12, 1, "海淀区"),
                new Region(21, 2, "浦东新区"),
                new Region(22, 2, "黄浦区")
            );

            // Act
            DropDownOptions cascade = DropDownOptions.buildLinkedOptions(
                provinces, 0, districts, 1,
                Region::getId, Region::getParentId,
                region -> region.getName()
            );

            // Assert
            assertThat(cascade.getOptions()).containsExactly("北京市", "上海市");
            assertThat(cascade.getNextOptions().get("北京市")).containsExactly("朝阳区", "海淀区");
            assertThat(cascade.getNextOptions().get("上海市")).containsExactly("浦东新区", "黄浦区");
        }
    }

    // 测试辅助类
    private static class Region {
        private final Integer id;
        private final Integer parentId;
        private final String name;

        Region(Integer id, Integer parentId, String name) {
            this.id = id;
            this.parentId = parentId;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public Integer getParentId() {
            return parentId;
        }

        public String getName() {
            return name;
        }
    }
}
