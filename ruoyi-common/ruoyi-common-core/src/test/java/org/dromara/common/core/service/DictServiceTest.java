package org.dromara.common.core.service;

import org.dromara.common.core.BaseUnitTest;
import org.dromara.common.core.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DictService 接口默认方法单元测试
 *
 * @author Test Team
 */
class DictServiceTest extends BaseUnitTest {

    /**
     * 测试用的 DictService 实现
     */
    private static class TestDictServiceImpl implements DictService {
        @Override
        public String getDictLabel(String dictType, String dictValue, String separator) {
            // 模拟实现：根据字典类型和值返回标签
            if ("sys_user_sex".equals(dictType)) {
                if ("0".equals(dictValue)) {
                    return "男";
                } else if ("1".equals(dictValue)) {
                    return "女";
                } else if ("0,1".equals(dictValue)) {
                    return "男" + separator + "女";
                }
            }
            return "";
        }

        @Override
        public String getDictValue(String dictType, String dictLabel, String separator) {
            // 模拟实现：根据字典类型和标签返回值
            if ("sys_user_sex".equals(dictType)) {
                if ("男".equals(dictLabel)) {
                    return "0";
                } else if ("女".equals(dictLabel)) {
                    return "1";
                } else if (("男" + separator + "女").equals(dictLabel)) {
                    return "0,1";
                }
            }
            return "";
        }

        @Override
        public Map<String, String> getAllDictByDictType(String dictType) {
            Map<String, String> result = new HashMap<>();
            if ("sys_user_sex".equals(dictType)) {
                result.put("0", "男");
                result.put("1", "女");
            }
            return result;
        }
    }

    private final DictService dictService = new TestDictServiceImpl();

    // ========== getDictLabel(dictType, dictValue) 默认方法测试 ==========

    @Test
    void shouldCallGetDictLabelWithDefaultSeparatorWhenUsingTwoParameterMethod() {
        // Arrange
        String dictType = "sys_user_sex";
        String dictValue = "0,1";

        // Act
        String result = dictService.getDictLabel(dictType, dictValue);

        // Assert
        assertThat(result).isEqualTo("男,女"); // 使用默认分隔符 ","
    }

    @Test
    void shouldReturnSingleLabelWhenSingleDictValue() {
        // Arrange
        String dictType = "sys_user_sex";
        String dictValue = "0";

        // Act
        String result = dictService.getDictLabel(dictType, dictValue);

        // Assert
        assertThat(result).isEqualTo("男");
    }

    @Test
    void shouldReturnEmptyWhenUnknownDictType() {
        // Arrange
        String dictType = "unknown_type";
        String dictValue = "0";

        // Act
        String result = dictService.getDictLabel(dictType, dictValue);

        // Assert
        assertThat(result).isEmpty();
    }

    // ========== getDictValue(dictType, dictLabel) 默认方法测试 ==========

    @Test
    void shouldCallGetDictValueWithDefaultSeparatorWhenUsingTwoParameterMethod() {
        // Arrange
        String dictType = "sys_user_sex";
        String dictLabel = "男,女";

        // Act
        String result = dictService.getDictValue(dictType, dictLabel);

        // Assert
        assertThat(result).isEqualTo("0,1"); // 使用默认分隔符 ","
    }

    @Test
    void shouldReturnSingleValueWhenSingleDictLabel() {
        // Arrange
        String dictType = "sys_user_sex";
        String dictLabel = "男";

        // Act
        String result = dictService.getDictValue(dictType, dictLabel);

        // Assert
        assertThat(result).isEqualTo("0");
    }

    @Test
    void shouldReturnEmptyWhenUnknownDictLabel() {
        // Arrange
        String dictType = "sys_user_sex";
        String dictLabel = "未知";

        // Act
        String result = dictService.getDictValue(dictType, dictLabel);

        // Assert
        assertThat(result).isEmpty();
    }

    // ========== 验证默认分隔符常量 ==========

    @Test
    void shouldUseCommaSeparatorAsDefaultSeparator() {
        // Arrange
        String dictType = "sys_user_sex";
        String dictValue = "0,1";

        // Act
        String resultWithDefault = dictService.getDictLabel(dictType, dictValue);
        String resultWithComma = dictService.getDictLabel(dictType, dictValue, StringUtils.SEPARATOR);

        // Assert
        assertThat(resultWithDefault)
            .as("默认方法应使用 StringUtils.SEPARATOR 作为分隔符")
            .isEqualTo(resultWithComma);
    }

    // ========== getAllDictByDictType 实现方法测试 ==========

    @Test
    void shouldReturnAllDictItemsWhenDictTypeExists() {
        // Arrange
        String dictType = "sys_user_sex";

        // Act
        Map<String, String> result = dictService.getAllDictByDictType(dictType);

        // Assert
        assertThat(result)
            .hasSize(2)
            .containsEntry("0", "男")
            .containsEntry("1", "女");
    }

    @Test
    void shouldReturnEmptyMapWhenDictTypeNotExists() {
        // Arrange
        String dictType = "unknown_type";

        // Act
        Map<String, String> result = dictService.getAllDictByDictType(dictType);

        // Assert
        assertThat(result).isEmpty();
    }
}
