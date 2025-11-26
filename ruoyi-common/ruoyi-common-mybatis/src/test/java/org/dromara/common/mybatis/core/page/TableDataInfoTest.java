package org.dromara.common.mybatis.core.page;

import static org.assertj.core.api.Assertions.assertThat;

import cn.hutool.http.HttpStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * TableDataInfo 表格分页数据测试
 *
 * @author Test Team
 */
@DisplayName("TableDataInfo 表格分页数据测试")
class TableDataInfoTest {

    @Nested
    @DisplayName("1. 构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造函数应该正常工作")
        void noArgsConstructorShouldWork() {
            // Act
            TableDataInfo<String> dataInfo = new TableDataInfo<>();

            // Assert
            assertThat(dataInfo).isNotNull();
            assertThat(dataInfo.getTotal()).isZero();
            assertThat(dataInfo.getRows()).isNull();
            assertThat(dataInfo.getCode()).isZero();
            assertThat(dataInfo.getMsg()).isNull();
        }

        @Test
        @DisplayName("有参构造函数应该正确设置属性")
        void parameterizedConstructorShouldSetProperties() {
            // Arrange
            List<String> data = Arrays.asList("A", "B", "C");

            // Act
            TableDataInfo<String> dataInfo = new TableDataInfo<>(data, 100L);

            // Assert
            assertThat(dataInfo.getRows()).isEqualTo(data);
            assertThat(dataInfo.getTotal()).isEqualTo(100L);
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
        }
    }

    @Nested
    @DisplayName("2. build(IPage) 静态方法测试")
    class BuildFromIPageTests {

        @Test
        @DisplayName("应该从Page对象构建TableDataInfo")
        void shouldBuildFromPageObject() {
            // Arrange
            Page<String> page = new Page<>(1, 10, 100);
            page.setRecords(Arrays.asList("A", "B", "C", "D", "E"));

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(page);

            // Assert
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
            assertThat(dataInfo.getRows()).hasSize(5);
            assertThat(dataInfo.getTotal()).isEqualTo(100);
        }

        @Test
        @DisplayName("应该正确处理空记录的Page对象")
        void shouldHandleEmptyPageRecords() {
            // Arrange
            Page<String> page = new Page<>(1, 10, 0);
            page.setRecords(Collections.emptyList());

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(page);

            // Assert
            assertThat(dataInfo.getRows()).isEmpty();
            assertThat(dataInfo.getTotal()).isZero();
        }

        @Test
        @DisplayName("应该保持Page对象的total值")
        void shouldPreservePageTotalValue() {
            // Arrange
            Page<String> page = new Page<>(2, 10, 250);
            page.setRecords(Arrays.asList("X", "Y"));

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(page);

            // Assert
            assertThat(dataInfo.getTotal()).isEqualTo(250);
            assertThat(dataInfo.getRows()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("3. build(List) 静态方法测试")
    class BuildFromListTests {

        @Test
        @DisplayName("应该从List构建TableDataInfo")
        void shouldBuildFromList() {
            // Arrange
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(list);

            // Assert
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
            assertThat(dataInfo.getRows()).isEqualTo(list);
            assertThat(dataInfo.getTotal()).isEqualTo(5);
        }

        @Test
        @DisplayName("应该正确处理空List")
        void shouldHandleEmptyList() {
            // Arrange
            List<String> emptyList = Collections.emptyList();

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(emptyList);

            // Assert
            assertThat(dataInfo.getRows()).isEmpty();
            assertThat(dataInfo.getTotal()).isZero();
        }

        @Test
        @DisplayName("total应该等于List的大小")
        void totalShouldEqualListSize() {
            // Arrange
            List<String> list = Arrays.asList("A", "B", "C", "D", "E", "F", "G");

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(list);

            // Assert
            assertThat(dataInfo.getTotal()).isEqualTo(list.size());
        }
    }

    @Nested
    @DisplayName("4. build() 无参静态方法测试")
    class BuildEmptyTests {

        @Test
        @DisplayName("应该构建空的TableDataInfo对象")
        void shouldBuildEmptyTableDataInfo() {
            // Act
            TableDataInfo<Object> dataInfo = TableDataInfo.build();

            // Assert
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
            assertThat(dataInfo.getRows()).isNull();
            assertThat(dataInfo.getTotal()).isZero();
        }

        @Test
        @DisplayName("返回的对象应该是新实例")
        void returnedObjectShouldBeNewInstance() {
            // Act
            TableDataInfo<Object> dataInfo1 = TableDataInfo.build();
            TableDataInfo<Object> dataInfo2 = TableDataInfo.build();

            // Assert
            assertThat(dataInfo1).isNotSameAs(dataInfo2);
        }
    }

    @Nested
    @DisplayName("5. build(List, IPage) 静态方法测试 - 假分页")
    class BuildFakePaginationTests {

        @Test
        @DisplayName("应该正确进行假分页 - 第1页")
        void shouldPerformFakePaginationFirstPage() {
            // Arrange
            List<Integer> fullList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Page<Integer> page = new Page<>(1, 3); // 第1页，每页3条

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(fullList, page);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly(1, 2, 3);
            assertThat(dataInfo.getTotal()).isEqualTo(10);
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
        }

        @Test
        @DisplayName("应该正确进行假分页 - 第2页")
        void shouldPerformFakePaginationSecondPage() {
            // Arrange
            List<Integer> fullList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Page<Integer> page = new Page<>(2, 3); // 第2页，每页3条

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(fullList, page);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly(4, 5, 6);
            assertThat(dataInfo.getTotal()).isEqualTo(10);
        }

        @Test
        @DisplayName("应该正确进行假分页 - 最后一页（不足一页）")
        void shouldPerformFakePaginationLastPage() {
            // Arrange
            List<Integer> fullList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Page<Integer> page = new Page<>(4, 3); // 第4页，每页3条

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(fullList, page);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly(10);
            assertThat(dataInfo.getTotal()).isEqualTo(10);
        }

        @Test
        @DisplayName("应该返回空结果 - 当List为空")
        void shouldReturnEmptyResult_WhenListIsEmpty() {
            // Arrange
            List<String> emptyList = Collections.emptyList();
            Page<String> page = new Page<>(1, 10);

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(emptyList, page);

            // Assert
            assertThat(dataInfo.getRows()).isNull();
            assertThat(dataInfo.getTotal()).isZero();
        }

        @Test
        @DisplayName("应该返回空结果 - 当List为null")
        void shouldReturnEmptyResult_WhenListIsNull() {
            // Arrange
            Page<String> page = new Page<>(1, 10);

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(null, page);

            // Assert
            assertThat(dataInfo.getRows()).isNull();
            assertThat(dataInfo.getTotal()).isZero();
        }

        @Test
        @DisplayName("应该正确处理单页数据")
        void shouldHandleSinglePageData() {
            // Arrange
            List<Integer> smallList = Arrays.asList(1, 2, 3);
            Page<Integer> page = new Page<>(1, 10); // 第1页，每页10条

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(smallList, page);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly(1, 2, 3);
            assertThat(dataInfo.getTotal()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("6. Lombok 注解测试")
    class LombokAnnotationsTests {

        @Test
        @DisplayName("setter 方法应该正常工作")
        void settersShouldWork() {
            // Arrange
            TableDataInfo<String> dataInfo = new TableDataInfo<>();
            List<String> rows = Arrays.asList("A", "B");

            // Act
            dataInfo.setTotal(100);
            dataInfo.setRows(rows);
            dataInfo.setCode(HttpStatus.HTTP_OK);
            dataInfo.setMsg("Success");

            // Assert
            assertThat(dataInfo.getTotal()).isEqualTo(100);
            assertThat(dataInfo.getRows()).isEqualTo(rows);
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("Success");
        }

        @Test
        @DisplayName("getter 方法应该正常工作")
        void gettersShouldWork() {
            // Arrange
            List<Integer> rows = Arrays.asList(1, 2, 3);
            TableDataInfo<Integer> dataInfo = new TableDataInfo<>(rows, 50);

            // Act & Assert
            assertThat(dataInfo.getRows()).isEqualTo(rows);
            assertThat(dataInfo.getTotal()).isEqualTo(50);
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
        }
    }

    @Nested
    @DisplayName("7. 序列化测试")
    class SerializableTests {

        @Test
        @DisplayName("TableDataInfo 应该实现 Serializable 接口")
        void shouldImplementSerializable() {
            assertThat(TableDataInfo.class).isAssignableTo(java.io.Serializable.class);
        }

        @Test
        @DisplayName("serialVersionUID 应该被定义")
        void shouldHaveSerialVersionUID() throws NoSuchFieldException {
            java.lang.reflect.Field field =
                    TableDataInfo.class.getDeclaredField("serialVersionUID");
            assertThat(field).isNotNull();
            assertThat(field.getType()).isEqualTo(long.class);
        }
    }

    @Nested
    @DisplayName("8. 泛型类型测试")
    class GenericTypeTests {

        @Test
        @DisplayName("应该支持String类型")
        void shouldSupportStringType() {
            // Arrange
            List<String> list = Arrays.asList("foo", "bar");

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(list);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly("foo", "bar");
        }

        @Test
        @DisplayName("应该支持Integer类型")
        void shouldSupportIntegerType() {
            // Arrange
            List<Integer> list = Arrays.asList(10, 20, 30);

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(list);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly(10, 20, 30);
        }

        @Test
        @DisplayName("应该支持自定义对象类型")
        void shouldSupportCustomObjectType() {
            // Arrange
            class User {
                String name;

                User(String name) {
                    this.name = name;
                }
            }
            List<User> users = Arrays.asList(new User("Alice"), new User("Bob"));

            // Act
            TableDataInfo<User> dataInfo = TableDataInfo.build(users);

            // Assert
            assertThat(dataInfo.getRows()).hasSize(2);
            assertThat(dataInfo.getTotal()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("9. 综合业务场景测试")
    class ComprehensiveScenarioTests {

        @Test
        @DisplayName("场景1: 从数据库查询结果构建分页数据")
        void scenario1_BuildFromDatabaseQuery() {
            // Arrange - 模拟数据库查询返回的Page对象
            Page<String> dbResult = new Page<>(1, 10, 156);
            dbResult.setRecords(Arrays.asList("User1", "User2", "User3", "User4", "User5"));

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(dbResult);

            // Assert
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
            assertThat(dataInfo.getRows()).hasSize(5);
            assertThat(dataInfo.getTotal()).isEqualTo(156);
        }

        @Test
        @DisplayName("场景2: 从内存列表构建完整数据")
        void scenario2_BuildFromMemoryList() {
            // Arrange - 从缓存或内存中获取的完整列表
            List<Integer> cacheData = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(cacheData);

            // Assert
            assertThat(dataInfo.getRows()).hasSize(10);
            assertThat(dataInfo.getTotal()).isEqualTo(10);
        }

        @Test
        @DisplayName("场景3: 内存分页（假分页）")
        void scenario3_InMemoryPagination() {
            // Arrange - 全量数据在内存中，需要分页
            List<String> allData = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J");
            Page<String> pageParam = new Page<>(2, 3); // 第2页，每页3条

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(allData, pageParam);

            // Assert
            assertThat(dataInfo.getRows()).containsExactly("D", "E", "F");
            assertThat(dataInfo.getTotal()).isEqualTo(10);
        }

        @Test
        @DisplayName("场景4: 空结果集处理")
        void scenario4_EmptyResultSet() {
            // Arrange - 查询无结果
            Page<String> emptyPage = new Page<>(1, 10, 0);
            emptyPage.setRecords(Collections.emptyList());

            // Act
            TableDataInfo<String> dataInfo = TableDataInfo.build(emptyPage);

            // Assert
            assertThat(dataInfo.getRows()).isEmpty();
            assertThat(dataInfo.getTotal()).isZero();
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
            assertThat(dataInfo.getMsg()).isEqualTo("查询成功");
        }

        @Test
        @DisplayName("场景5: 构造函数方式构建（向后兼容）")
        void scenario5_ConstructorStyleBuilding() {
            // Arrange
            List<Double> prices = Arrays.asList(19.99, 29.99, 39.99);

            // Act
            TableDataInfo<Double> dataInfo = new TableDataInfo<>(prices, 150L);

            // Assert
            assertThat(dataInfo.getRows()).hasSize(3);
            assertThat(dataInfo.getTotal()).isEqualTo(150L);
            assertThat(dataInfo.getCode()).isEqualTo(HttpStatus.HTTP_OK);
        }
    }

    @Nested
    @DisplayName("10. 边界条件测试")
    class BoundaryConditionTests {

        @Test
        @DisplayName("应该处理超大total值")
        void shouldHandleLargeTotalValue() {
            // Arrange
            List<String> data = Arrays.asList("A");
            long largeTotal = Long.MAX_VALUE;

            // Act
            TableDataInfo<String> dataInfo = new TableDataInfo<>(data, largeTotal);

            // Assert
            assertThat(dataInfo.getTotal()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("应该处理total为0的情况")
        void shouldHandleZeroTotal() {
            // Arrange
            List<String> emptyData = Collections.emptyList();

            // Act
            TableDataInfo<String> dataInfo = new TableDataInfo<>(emptyData, 0L);

            // Assert
            assertThat(dataInfo.getTotal()).isZero();
        }

        @Test
        @DisplayName("假分页应该处理超出范围的页码")
        void fakePaginationShouldHandleOutOfRangePageNum() {
            // Arrange
            List<Integer> data = Arrays.asList(1, 2, 3);
            Page<Integer> page = new Page<>(10, 10); // 第10页，但只有3条数据

            // Act
            TableDataInfo<Integer> dataInfo = TableDataInfo.build(data, page);

            // Assert
            assertThat(dataInfo.getRows()).isEmpty(); // 超出范围，返回空列表
            assertThat(dataInfo.getTotal()).isEqualTo(3);
        }
    }
}
