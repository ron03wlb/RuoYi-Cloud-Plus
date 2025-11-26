package org.dromara.common.mybatis.core.page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * PageQuery 分页查询测试
 *
 * @author Test Team
 */
@DisplayName("PageQuery 分页查询测试")
class PageQueryTest {

    @Nested
    @DisplayName("1. 默认值常量测试")
    class DefaultConstantsTests {

        @Test
        @DisplayName("默认页码应该为 1")
        void defaultPageNumShouldBeOne() {
            assertThat(PageQuery.DEFAULT_PAGE_NUM).isEqualTo(1);
        }

        @Test
        @DisplayName("默认页面大小应该为 Integer.MAX_VALUE")
        void defaultPageSizeShouldBeMaxValue() {
            assertThat(PageQuery.DEFAULT_PAGE_SIZE).isEqualTo(Integer.MAX_VALUE);
        }
    }

    @Nested
    @DisplayName("2. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造函数应该正常工作")
        void noArgsConstructorShouldWork() {
            // Act
            PageQuery query = new PageQuery();

            // Assert
            assertThat(query).isNotNull();
            assertThat(query.getPageNum()).isNull();
            assertThat(query.getPageSize()).isNull();
        }

        @Test
        @DisplayName("有参构造函数应该正确设置属性")
        void parameterizedConstructorShouldSetProperties() {
            // Act
            PageQuery query = new PageQuery(10, 2);

            // Assert
            assertThat(query.getPageSize()).isEqualTo(10);
            assertThat(query.getPageNum()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("3. build 方法测试 - 基本分页")
    class BuildMethodBasicTests {

        @Test
        @DisplayName("应该使用默认值 - 当pageNum和pageSize都为null")
        void shouldUseDefaults_WhenBothAreNull() {
            // Arrange
            PageQuery query = new PageQuery();

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getCurrent()).isEqualTo(PageQuery.DEFAULT_PAGE_NUM);
            assertThat(page.getSize()).isEqualTo(PageQuery.DEFAULT_PAGE_SIZE);
        }

        @Test
        @DisplayName("应该使用指定的pageNum和pageSize")
        void shouldUseSpecifiedValues() {
            // Arrange
            PageQuery query = new PageQuery(20, 3);

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getCurrent()).isEqualTo(3);
            assertThat(page.getSize()).isEqualTo(20);
        }

        @Test
        @DisplayName("应该修正pageNum为默认值 - 当pageNum小于等于0")
        void shouldCorrectPageNum_WhenLessThanOrEqualToZero() {
            // Arrange
            PageQuery query1 = new PageQuery(10, 0);
            PageQuery query2 = new PageQuery(10, -1);

            // Act
            Page<?> page1 = query1.build();
            Page<?> page2 = query2.build();

            // Assert
            assertThat(page1.getCurrent()).isEqualTo(PageQuery.DEFAULT_PAGE_NUM);
            assertThat(page2.getCurrent()).isEqualTo(PageQuery.DEFAULT_PAGE_NUM);
        }

        @Test
        @DisplayName("应该使用默认pageSize - 当pageSize为null")
        void shouldUseDefaultPageSize_WhenNull() {
            // Arrange
            PageQuery query = new PageQuery();
            query.setPageNum(2);

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getSize()).isEqualTo(PageQuery.DEFAULT_PAGE_SIZE);
        }
    }

    @Nested
    @DisplayName("4. build 方法测试 - 单字段排序")
    class BuildMethodSingleFieldOrderTests {

        @Test
        @DisplayName("应该添加升序排序 - 单个字段")
        void shouldAddAscOrder_SingleField() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id");
            query.setIsAsc("asc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).hasSize(1);
            assertThat(page.orders().get(0).getColumn()).isEqualTo("id");
            assertThat(page.orders().get(0).isAsc()).isTrue();
        }

        @Test
        @DisplayName("应该添加降序排序 - 单个字段")
        void shouldAddDescOrder_SingleField() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("createTime");
            query.setIsAsc("desc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).hasSize(1);
            assertThat(page.orders().get(0).getColumn()).isEqualTo("create_time");
            assertThat(page.orders().get(0).isAsc()).isFalse();
        }

        @Test
        @DisplayName("应该转换驼峰为下划线 - 排序字段")
        void shouldConvertCamelCaseToUnderscore() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("userName");
            query.setIsAsc("asc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders().get(0).getColumn()).isEqualTo("user_name");
        }

        @Test
        @DisplayName("应该兼容前端排序类型 - ascending")
        void shouldSupportFrontendAscending() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id");
            query.setIsAsc("ascending");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders().get(0).isAsc()).isTrue();
        }

        @Test
        @DisplayName("应该兼容前端排序类型 - descending")
        void shouldSupportFrontendDescending() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id");
            query.setIsAsc("descending");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders().get(0).isAsc()).isFalse();
        }
    }

    @Nested
    @DisplayName("5. build 方法测试 - 多字段排序")
    class BuildMethodMultipleFieldsOrderTests {

        @Test
        @DisplayName("应该支持多字段同向排序 - 升序")
        void shouldSupportMultipleFieldsSameDirectionAsc() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id,createTime");
            query.setIsAsc("asc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).hasSize(2);
            assertThat(page.orders().get(0).getColumn()).isEqualTo("id");
            assertThat(page.orders().get(0).isAsc()).isTrue();
            assertThat(page.orders().get(1).getColumn()).isEqualTo("create_time");
            assertThat(page.orders().get(1).isAsc()).isTrue();
        }

        @Test
        @DisplayName("应该支持多字段同向排序 - 降序")
        void shouldSupportMultipleFieldsSameDirectionDesc() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id,userName");
            query.setIsAsc("desc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).hasSize(2);
            assertThat(page.orders().get(0).isAsc()).isFalse();
            assertThat(page.orders().get(1).isAsc()).isFalse();
        }

        @Test
        @DisplayName("应该支持多字段不同方向排序")
        void shouldSupportMultipleFieldsDifferentDirections() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id,createTime,userName");
            query.setIsAsc("asc,desc,asc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).hasSize(3);
            assertThat(page.orders().get(0).getColumn()).isEqualTo("id");
            assertThat(page.orders().get(0).isAsc()).isTrue();
            assertThat(page.orders().get(1).getColumn()).isEqualTo("create_time");
            assertThat(page.orders().get(1).isAsc()).isFalse();
            assertThat(page.orders().get(2).getColumn()).isEqualTo("user_name");
            assertThat(page.orders().get(2).isAsc()).isTrue();
        }
    }

    @Nested
    @DisplayName("6. build 方法测试 - 排序异常情况")
    class BuildMethodOrderExceptionTests {

        @Test
        @DisplayName("应该抛出异常 - 当排序方向数量不匹配字段数量")
        void shouldThrowException_WhenDirectionCountMismatch() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id,createTime,userName");
            query.setIsAsc("asc,desc"); // 3个字段，2个方向

            // Act & Assert
            assertThatThrownBy(query::build)
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("排序参数有误");
        }

        @Test
        @DisplayName("应该抛出异常 - 当排序方向无效")
        void shouldThrowException_WhenInvalidDirection() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id");
            query.setIsAsc("invalid");

            // Act & Assert
            assertThatThrownBy(query::build)
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("排序参数有误");
        }

        @Test
        @DisplayName("不应该添加排序 - 当orderByColumn为空")
        void shouldNotAddOrder_WhenOrderByColumnIsEmpty() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("");
            query.setIsAsc("asc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).isEmpty();
        }

        @Test
        @DisplayName("不应该添加排序 - 当isAsc为空")
        void shouldNotAddOrder_WhenIsAscIsEmpty() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("id");
            query.setIsAsc("");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).isEmpty();
        }

        @Test
        @DisplayName("不应该添加排序 - 当orderByColumn为null")
        void shouldNotAddOrder_WhenOrderByColumnIsNull() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setIsAsc("asc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).isEmpty();
        }
    }

    @Nested
    @DisplayName("7. getFirstNum 方法测试")
    class GetFirstNumMethodTests {

        @ParameterizedTest
        @CsvSource({
            "10, 1, 0", // 第1页，每页10条，起始索引0
            "10, 2, 10", // 第2页，每页10条，起始索引10
            "10, 3, 20", // 第3页，每页10条，起始索引20
            "20, 1, 0", // 第1页，每页20条，起始索引0
            "20, 5, 80", // 第5页，每页20条，起始索引80
            "1, 100, 99" // 第100页，每页1条，起始索引99
        })
        @DisplayName("应该正确计算起始索引")
        void shouldCalculateCorrectFirstNum(int pageSize, int pageNum, int expected) {
            // Arrange
            PageQuery query = new PageQuery(pageSize, pageNum);

            // Act
            Integer result = query.getFirstNum();

            // Assert
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("8. Lombok 注解测试")
    class LombokAnnotationsTests {

        @Test
        @DisplayName("setter 方法应该正常工作")
        void settersShouldWork() {
            // Arrange
            PageQuery query = new PageQuery();

            // Act
            query.setPageSize(50);
            query.setPageNum(5);
            query.setOrderByColumn("id");
            query.setIsAsc("desc");

            // Assert
            assertThat(query.getPageSize()).isEqualTo(50);
            assertThat(query.getPageNum()).isEqualTo(5);
            assertThat(query.getOrderByColumn()).isEqualTo("id");
            assertThat(query.getIsAsc()).isEqualTo("desc");
        }

        @Test
        @DisplayName("getter 方法应该正常工作")
        void gettersShouldWork() {
            // Arrange
            PageQuery query = new PageQuery(30, 2);
            query.setOrderByColumn("userName");
            query.setIsAsc("asc");

            // Act & Assert
            assertThat(query.getPageSize()).isEqualTo(30);
            assertThat(query.getPageNum()).isEqualTo(2);
            assertThat(query.getOrderByColumn()).isEqualTo("userName");
            assertThat(query.getIsAsc()).isEqualTo("asc");
        }
    }

    @Nested
    @DisplayName("9. 序列化测试")
    class SerializableTests {

        @Test
        @DisplayName("PageQuery 应该实现 Serializable 接口")
        void shouldImplementSerializable() {
            assertThat(PageQuery.class).isAssignableTo(java.io.Serializable.class);
        }

        @Test
        @DisplayName("serialVersionUID 应该被定义")
        void shouldHaveSerialVersionUID() throws NoSuchFieldException {
            java.lang.reflect.Field field = PageQuery.class.getDeclaredField("serialVersionUID");
            assertThat(field).isNotNull();
            assertThat(field.getType()).isEqualTo(long.class);
        }
    }

    @Nested
    @DisplayName("10. 综合业务场景测试")
    class ComprehensiveScenarioTests {

        @Test
        @DisplayName("场景1: 首页查询，每页10条，按创建时间降序")
        void scenario1_FirstPageWithDescOrder() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("createTime");
            query.setIsAsc("desc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getCurrent()).isEqualTo(1);
            assertThat(page.getSize()).isEqualTo(10);
            assertThat(page.orders()).hasSize(1);
            assertThat(page.orders().get(0).getColumn()).isEqualTo("create_time");
            assertThat(page.orders().get(0).isAsc()).isFalse();
        }

        @Test
        @DisplayName("场景2: 第3页查询，每页20条，无排序")
        void scenario2_ThirdPageWithoutOrder() {
            // Arrange
            PageQuery query = new PageQuery(20, 3);

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getCurrent()).isEqualTo(3);
            assertThat(page.getSize()).isEqualTo(20);
            assertThat(page.orders()).isEmpty();
            assertThat(query.getFirstNum()).isEqualTo(40);
        }

        @Test
        @DisplayName("场景3: 默认分页（查询全部）")
        void scenario3_DefaultPagination() {
            // Arrange
            PageQuery query = new PageQuery();

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getCurrent()).isEqualTo(PageQuery.DEFAULT_PAGE_NUM);
            assertThat(page.getSize()).isEqualTo(PageQuery.DEFAULT_PAGE_SIZE);
        }

        @Test
        @DisplayName("场景4: 多字段复杂排序（ID升序，创建时间降序）")
        void scenario4_ComplexMultiFieldOrder() {
            // Arrange
            PageQuery query = new PageQuery(15, 2);
            query.setOrderByColumn("id,createTime");
            query.setIsAsc("asc,desc");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.getCurrent()).isEqualTo(2);
            assertThat(page.getSize()).isEqualTo(15);
            assertThat(page.orders()).hasSize(2);

            OrderItem firstOrder = page.orders().get(0);
            assertThat(firstOrder.getColumn()).isEqualTo("id");
            assertThat(firstOrder.isAsc()).isTrue();

            OrderItem secondOrder = page.orders().get(1);
            assertThat(secondOrder.getColumn()).isEqualTo("create_time");
            assertThat(secondOrder.isAsc()).isFalse();
        }

        @Test
        @DisplayName("场景5: 使用前端排序类型（ascending/descending）")
        void scenario5_FrontendSortingTypes() {
            // Arrange
            PageQuery query = new PageQuery(10, 1);
            query.setOrderByColumn("userName,createTime");
            query.setIsAsc("ascending,descending");

            // Act
            Page<?> page = query.build();

            // Assert
            assertThat(page.orders()).hasSize(2);
            assertThat(page.orders().get(0).isAsc()).isTrue();
            assertThat(page.orders().get(1).isAsc()).isFalse();
        }
    }
}
