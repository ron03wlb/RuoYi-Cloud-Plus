package org.dromara.common.mybatis.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * BaseEntity 基础实体类测试
 *
 * @author Test Team
 */
@DisplayName("BaseEntity 基础实体类测试")
class BaseEntityTest {

    @Nested
    @DisplayName("1. 对象创建和基本属性测试")
    class ObjectCreationTests {

        @Test
        @DisplayName("应该能够创建BaseEntity实例")
        void shouldCreateBaseEntityInstance() {
            // Act
            BaseEntity entity = new BaseEntity();

            // Assert
            assertThat(entity).isNotNull();
        }

        @Test
        @DisplayName("params 字段应该初始化为空Map")
        void paramsFieldShouldBeInitializedAsEmptyMap() {
            // Act
            BaseEntity entity = new BaseEntity();

            // Assert
            assertThat(entity.getParams()).isNotNull();
            assertThat(entity.getParams()).isEmpty();
        }

        @Test
        @DisplayName("其他字段应该初始化为null")
        void otherFieldsShouldBeInitializedAsNull() {
            // Act
            BaseEntity entity = new BaseEntity();

            // Assert
            assertThat(entity.getSearchValue()).isNull();
            assertThat(entity.getCreateDept()).isNull();
            assertThat(entity.getCreateBy()).isNull();
            assertThat(entity.getCreateTime()).isNull();
            assertThat(entity.getUpdateBy()).isNull();
            assertThat(entity.getUpdateTime()).isNull();
        }
    }

    @Nested
    @DisplayName("2. Getter 和 Setter 测试")
    class GetterSetterTests {

        @Test
        @DisplayName("searchValue 的 getter 和 setter 应该正常工作")
        void searchValueGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act
            entity.setSearchValue("test search");

            // Assert
            assertThat(entity.getSearchValue()).isEqualTo("test search");
        }

        @Test
        @DisplayName("createDept 的 getter 和 setter 应该正常工作")
        void createDeptGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act
            entity.setCreateDept(100L);

            // Assert
            assertThat(entity.getCreateDept()).isEqualTo(100L);
        }

        @Test
        @DisplayName("createBy 的 getter 和 setter 应该正常工作")
        void createByGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act
            entity.setCreateBy(1001L);

            // Assert
            assertThat(entity.getCreateBy()).isEqualTo(1001L);
        }

        @Test
        @DisplayName("createTime 的 getter 和 setter 应该正常工作")
        void createTimeGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            Date now = new Date();

            // Act
            entity.setCreateTime(now);

            // Assert
            assertThat(entity.getCreateTime()).isEqualTo(now);
        }

        @Test
        @DisplayName("updateBy 的 getter 和 setter 应该正常工作")
        void updateByGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act
            entity.setUpdateBy(2001L);

            // Assert
            assertThat(entity.getUpdateBy()).isEqualTo(2001L);
        }

        @Test
        @DisplayName("updateTime 的 getter 和 setter 应该正常工作")
        void updateTimeGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            Date now = new Date();

            // Act
            entity.setUpdateTime(now);

            // Assert
            assertThat(entity.getUpdateTime()).isEqualTo(now);
        }

        @Test
        @DisplayName("params 的 getter 和 setter 应该正常工作")
        void paramsGetterSetterShouldWork() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            Map<String, Object> params = new HashMap<>();
            params.put("key1", "value1");
            params.put("key2", 123);

            // Act
            entity.setParams(params);

            // Assert
            assertThat(entity.getParams()).isEqualTo(params);
            assertThat(entity.getParams().get("key1")).isEqualTo("value1");
            assertThat(entity.getParams().get("key2")).isEqualTo(123);
        }
    }

    @Nested
    @DisplayName("3. MyBatis-Plus @TableField 注解测试")
    class TableFieldAnnotationTests {

        @Test
        @DisplayName("searchValue 字段应该标记为 exist=false")
        void searchValueShouldBeMarkedAsNonExistent() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("searchValue");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.exist()).isFalse();
        }

        @Test
        @DisplayName("createDept 字段应该标记为 INSERT 时自动填充")
        void createDeptShouldBeMarkedAsInsertFill() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("createDept");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.fill()).isEqualTo(FieldFill.INSERT);
        }

        @Test
        @DisplayName("createBy 字段应该标记为 INSERT 时自动填充")
        void createByShouldBeMarkedAsInsertFill() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("createBy");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.fill()).isEqualTo(FieldFill.INSERT);
        }

        @Test
        @DisplayName("createTime 字段应该标记为 INSERT 时自动填充")
        void createTimeShouldBeMarkedAsInsertFill() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("createTime");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.fill()).isEqualTo(FieldFill.INSERT);
        }

        @Test
        @DisplayName("updateBy 字段应该标记为 INSERT_UPDATE 时自动填充")
        void updateByShouldBeMarkedAsInsertUpdateFill() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("updateBy");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.fill()).isEqualTo(FieldFill.INSERT_UPDATE);
        }

        @Test
        @DisplayName("updateTime 字段应该标记为 INSERT_UPDATE 时自动填充")
        void updateTimeShouldBeMarkedAsInsertUpdateFill() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("updateTime");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.fill()).isEqualTo(FieldFill.INSERT_UPDATE);
        }

        @Test
        @DisplayName("params 字段应该标记为 exist=false")
        void paramsShouldBeMarkedAsNonExistent() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("params");

            // Act
            TableField annotation = field.getAnnotation(TableField.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.exist()).isFalse();
        }
    }

    @Nested
    @DisplayName("4. Jackson @JsonIgnore 注解测试")
    class JsonIgnoreAnnotationTests {

        @Test
        @DisplayName("searchValue 字段应该标记为 @JsonIgnore")
        void searchValueShouldBeMarkedAsJsonIgnore() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("searchValue");

            // Act
            JsonIgnore annotation = field.getAnnotation(JsonIgnore.class);

            // Assert
            assertThat(annotation).isNotNull();
        }
    }

    @Nested
    @DisplayName("5. Jackson @JsonInclude 注解测试")
    class JsonIncludeAnnotationTests {

        @Test
        @DisplayName("params 字段应该标记为 @JsonInclude(NON_EMPTY)")
        void paramsShouldBeMarkedAsJsonIncludeNonEmpty() throws NoSuchFieldException {
            // Arrange
            Field field = BaseEntity.class.getDeclaredField("params");

            // Act
            JsonInclude annotation = field.getAnnotation(JsonInclude.class);

            // Assert
            assertThat(annotation).isNotNull();
            assertThat(annotation.value()).isEqualTo(JsonInclude.Include.NON_EMPTY);
        }
    }

    @Nested
    @DisplayName("6. 序列化测试")
    class SerializableTests {

        @Test
        @DisplayName("BaseEntity 应该实现 Serializable 接口")
        void shouldImplementSerializable() {
            assertThat(BaseEntity.class).isAssignableTo(java.io.Serializable.class);
        }

        @Test
        @DisplayName("serialVersionUID 应该被定义")
        void shouldHaveSerialVersionUID() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("serialVersionUID");
            assertThat(field).isNotNull();
            assertThat(field.getType()).isEqualTo(long.class);
        }
    }

    @Nested
    @DisplayName("7. params Map 操作测试")
    class ParamsMapOperationTests {

        @Test
        @DisplayName("应该能够向 params 添加元素")
        void shouldBeAbleToAddElementsToParams() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act
            entity.getParams().put("userId", 100L);
            entity.getParams().put("userName", "admin");

            // Assert
            assertThat(entity.getParams()).hasSize(2);
            assertThat(entity.getParams().get("userId")).isEqualTo(100L);
            assertThat(entity.getParams().get("userName")).isEqualTo("admin");
        }

        @Test
        @DisplayName("应该能够从 params 移除元素")
        void shouldBeAbleToRemoveElementsFromParams() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            entity.getParams().put("key1", "value1");
            entity.getParams().put("key2", "value2");

            // Act
            entity.getParams().remove("key1");

            // Assert
            assertThat(entity.getParams()).hasSize(1);
            assertThat(entity.getParams()).doesNotContainKey("key1");
        }

        @Test
        @DisplayName("应该能够清空 params")
        void shouldBeAbleToClearParams() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            entity.getParams().put("key", "value");

            // Act
            entity.getParams().clear();

            // Assert
            assertThat(entity.getParams()).isEmpty();
        }

        @Test
        @DisplayName("应该能够替换整个 params Map")
        void shouldBeAbleToReplaceParamsMap() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            entity.getParams().put("old", "value");

            Map<String, Object> newParams = new HashMap<>();
            newParams.put("new", "value");

            // Act
            entity.setParams(newParams);

            // Assert
            assertThat(entity.getParams()).isEqualTo(newParams);
            assertThat(entity.getParams()).doesNotContainKey("old");
            assertThat(entity.getParams()).containsKey("new");
        }
    }

    @Nested
    @DisplayName("8. 字段类型验证测试")
    class FieldTypeValidationTests {

        @Test
        @DisplayName("searchValue 字段类型应该是 String")
        void searchValueTypeShouldBeString() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("searchValue");
            assertThat(field.getType()).isEqualTo(String.class);
        }

        @Test
        @DisplayName("createDept 字段类型应该是 Long")
        void createDeptTypeShouldBeLong() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("createDept");
            assertThat(field.getType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("createBy 字段类型应该是 Long")
        void createByTypeShouldBeLong() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("createBy");
            assertThat(field.getType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("createTime 字段类型应该是 Date")
        void createTimeTypeShouldBeDate() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("createTime");
            assertThat(field.getType()).isEqualTo(Date.class);
        }

        @Test
        @DisplayName("updateBy 字段类型应该是 Long")
        void updateByTypeShouldBeLong() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("updateBy");
            assertThat(field.getType()).isEqualTo(Long.class);
        }

        @Test
        @DisplayName("updateTime 字段类型应该是 Date")
        void updateTimeTypeShouldBeDate() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("updateTime");
            assertThat(field.getType()).isEqualTo(Date.class);
        }

        @Test
        @DisplayName("params 字段类型应该是 Map")
        void paramsTypeShouldBeMap() throws NoSuchFieldException {
            Field field = BaseEntity.class.getDeclaredField("params");
            assertThat(field.getType()).isEqualTo(Map.class);
        }
    }

    @Nested
    @DisplayName("9. 继承场景测试")
    class InheritanceScenarioTests {

        static class TestEntity extends BaseEntity {
            private String name;
            private Integer age;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public Integer getAge() {
                return age;
            }

            public void setAge(Integer age) {
                this.age = age;
            }
        }

        @Test
        @DisplayName("子类应该继承所有BaseEntity字段")
        void subclassShouldInheritAllBaseEntityFields() {
            // Arrange & Act
            TestEntity entity = new TestEntity();
            entity.setName("Test");
            entity.setAge(25);
            entity.setCreateBy(100L);
            entity.setCreateTime(new Date());

            // Assert
            assertThat(entity.getName()).isEqualTo("Test");
            assertThat(entity.getAge()).isEqualTo(25);
            assertThat(entity.getCreateBy()).isEqualTo(100L);
            assertThat(entity.getCreateTime()).isNotNull();
            assertThat(entity.getParams()).isNotNull();
        }

        @Test
        @DisplayName("子类应该能够使用 params 功能")
        void subclassShouldBeAbleToUseParams() {
            // Arrange & Act
            TestEntity entity = new TestEntity();
            entity.getParams().put("customKey", "customValue");

            // Assert
            assertThat(entity.getParams()).containsEntry("customKey", "customValue");
        }
    }

    @Nested
    @DisplayName("10. 真实业务场景测试")
    class RealBusinessScenarioTests {

        @Test
        @DisplayName("场景1: 新增实体时设置创建信息")
        void scenario1_SetCreationInfo() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            Long userId = 1001L;
            Long deptId = 100L;
            Date now = new Date();

            // Act
            entity.setCreateBy(userId);
            entity.setCreateDept(deptId);
            entity.setCreateTime(now);

            // Assert
            assertThat(entity.getCreateBy()).isEqualTo(userId);
            assertThat(entity.getCreateDept()).isEqualTo(deptId);
            assertThat(entity.getCreateTime()).isEqualTo(now);
        }

        @Test
        @DisplayName("场景2: 更新实体时设置更新信息")
        void scenario2_SetUpdateInfo() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            entity.setCreateBy(1001L);
            entity.setCreateTime(new Date());

            Long updater = 2001L;
            Date updateTime = new Date();

            // Act
            entity.setUpdateBy(updater);
            entity.setUpdateTime(updateTime);

            // Assert
            assertThat(entity.getUpdateBy()).isEqualTo(updater);
            assertThat(entity.getUpdateTime()).isEqualTo(updateTime);
        }

        @Test
        @DisplayName("场景3: 使用 params 存储额外查询参数")
        void scenario3_UseParamsForExtraQueryParameters() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act - 设置查询时间范围
            entity.getParams().put("beginTime", "2025-01-01");
            entity.getParams().put("endTime", "2025-12-31");
            entity.getParams().put("status", 1);

            // Assert
            assertThat(entity.getParams()).hasSize(3);
            assertThat(entity.getParams().get("beginTime")).isEqualTo("2025-01-01");
            assertThat(entity.getParams().get("endTime")).isEqualTo("2025-12-31");
            assertThat(entity.getParams().get("status")).isEqualTo(1);
        }

        @Test
        @DisplayName("场景4: searchValue 用于全局搜索")
        void scenario4_UseSearchValueForGlobalSearch() {
            // Arrange
            BaseEntity entity = new BaseEntity();

            // Act
            entity.setSearchValue("admin");

            // Assert
            assertThat(entity.getSearchValue()).isEqualTo("admin");
        }

        @Test
        @DisplayName("场景5: 完整的实体生命周期")
        void scenario5_CompleteEntityLifecycle() {
            // Arrange
            BaseEntity entity = new BaseEntity();
            Date createDate = new Date();
            Date updateDate = new Date();

            // Act - 创建
            entity.setCreateDept(100L);
            entity.setCreateBy(1001L);
            entity.setCreateTime(createDate);

            // Act - 第一次更新
            entity.setUpdateBy(2001L);
            entity.setUpdateTime(updateDate);

            // Act - 添加查询参数
            entity.getParams().put("filterType", "active");

            // Assert
            assertThat(entity.getCreateDept()).isEqualTo(100L);
            assertThat(entity.getCreateBy()).isEqualTo(1001L);
            assertThat(entity.getCreateTime()).isEqualTo(createDate);
            assertThat(entity.getUpdateBy()).isEqualTo(2001L);
            assertThat(entity.getUpdateTime()).isEqualTo(updateDate);
            assertThat(entity.getParams()).containsEntry("filterType", "active");
        }
    }
}
