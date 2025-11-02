package org.dromara.common.core.utils;

import cn.hutool.core.collection.CollUtil;
import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.domain.Order;
import org.dromara.common.core.domain.OrderVO;
import org.dromara.common.core.domain.User;
import org.dromara.common.core.domain.UserVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * MapstructUtils 集成测试
 *
 * @author Test Team
 */
@DisplayName("MapstructUtils 集成测试")
class MapstructUtilsIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("convert(source, Class) 方法测试 - 对象到对象转换")
    class ConvertObjectToClassTest {

        @Test
        @DisplayName("应该成功将实体转换为VO")
        void shouldConvertEntityToVOWhenValidInput() {
            // Arrange
            User user = new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now());

            // Act
            UserVO result = MapstructUtils.convert(user, UserVO.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(user.getId());
            assertThat(result.getUsername()).isEqualTo(user.getUsername());
            assertThat(result.getEmail()).isEqualTo(user.getEmail());
            assertThat(result.getAge()).isEqualTo(user.getAge());
            assertThat(result.getCreateTime()).isEqualTo(user.getCreateTime());
        }

        @Test
        @DisplayName("应该成功将VO转换为实体")
        void shouldConvertVOToEntityWhenValidInput() {
            // Arrange
            UserVO userVO = new UserVO(2L, "lisi", "lisi@example.com", 30, LocalDateTime.now());

            // Act
            User result = MapstructUtils.convert(userVO, User.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(userVO.getId());
            assertThat(result.getUsername()).isEqualTo(userVO.getUsername());
            assertThat(result.getEmail()).isEqualTo(userVO.getEmail());
            assertThat(result.getAge()).isEqualTo(userVO.getAge());
            assertThat(result.getCreateTime()).isEqualTo(userVO.getCreateTime());
        }

        @Test
        @DisplayName("当源对象为null时应返回null")
        void shouldReturnNullWhenSourceIsNull() {
            // Act
            UserVO result = MapstructUtils.convert((User) null, UserVO.class);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("当目标类为null时应返回null")
        void shouldReturnNullWhenDescClassIsNull() {
            // Arrange
            User user = new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now());

            // Act
            UserVO result = MapstructUtils.convert(user, (Class<UserVO>) null);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该正确转换复杂对象")
        void shouldConvertComplexObjectWhenValidInput() {
            // Arrange
            Order order = new Order(100L, "ORDER-2024-001", 1L, new BigDecimal("999.99"), "PAID");

            // Act
            OrderVO result = MapstructUtils.convert(order, OrderVO.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getOrderId()).isEqualTo(order.getOrderId());
            assertThat(result.getOrderNo()).isEqualTo(order.getOrderNo());
            assertThat(result.getUserId()).isEqualTo(order.getUserId());
            assertThat(result.getTotalAmount()).isEqualByComparingTo(order.getTotalAmount());
            assertThat(result.getStatus()).isEqualTo(order.getStatus());
        }
    }

    @Nested
    @DisplayName("convert(source, desc) 方法测试 - 对象到对象赋值")
    class ConvertObjectToObjectTest {

        @Test
        @DisplayName("应该成功将源对象属性赋值到目标对象")
        void shouldAssignPropertiesWhenValidInput() {
            // Arrange
            User source = new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now());
            UserVO target = new UserVO();

            // Act
            UserVO result = MapstructUtils.convert(source, target);

            // Assert
            assertThat(result).isSameAs(target); // 应该返回同一个对象
            assertThat(result.getId()).isEqualTo(source.getId());
            assertThat(result.getUsername()).isEqualTo(source.getUsername());
            assertThat(result.getEmail()).isEqualTo(source.getEmail());
            assertThat(result.getAge()).isEqualTo(source.getAge());
            assertThat(result.getCreateTime()).isEqualTo(source.getCreateTime());
        }

        @Test
        @DisplayName("应该覆盖目标对象的现有属性")
        void shouldOverrideExistingPropertiesWhenTargetHasData() {
            // Arrange
            User source = new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now());
            UserVO target = new UserVO(999L, "oldname", "old@example.com", 99, LocalDateTime.now().minusYears(1));

            // Act
            UserVO result = MapstructUtils.convert(source, target);

            // Assert
            assertThat(result).isSameAs(target);
            assertThat(result.getId()).isEqualTo(source.getId()); // 应该被覆盖
            assertThat(result.getUsername()).isEqualTo(source.getUsername());
            assertThat(result.getEmail()).isEqualTo(source.getEmail());
            assertThat(result.getAge()).isEqualTo(source.getAge());
        }

        @Test
        @DisplayName("当源对象为null时应返回null")
        void shouldReturnNullWhenSourceIsNull() {
            // Arrange
            UserVO target = new UserVO();

            // Act
            UserVO result = MapstructUtils.convert(null, target);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("当目标对象为null时应返回null")
        void shouldReturnNullWhenTargetIsNull() {
            // Arrange
            User source = new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now());

            // Act
            UserVO result = MapstructUtils.convert(source, (UserVO) null);

            // Assert
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("convert(List, Class) 方法测试 - 列表转换")
    class ConvertListTest {

        @Test
        @DisplayName("应该成功转换非空列表")
        void shouldConvertNonEmptyListWhenValidInput() {
            // Arrange
            List<User> users = Arrays.asList(
                new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now()),
                new User(2L, "lisi", "lisi@example.com", 30, LocalDateTime.now()),
                new User(3L, "wangwu", "wangwu@example.com", 35, LocalDateTime.now())
            );

            // Act
            List<UserVO> result = MapstructUtils.convert(users, UserVO.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getUsername()).isEqualTo("zhangsan");
            assertThat(result.get(1).getId()).isEqualTo(2L);
            assertThat(result.get(1).getUsername()).isEqualTo("lisi");
            assertThat(result.get(2).getId()).isEqualTo(3L);
            assertThat(result.get(2).getUsername()).isEqualTo("wangwu");
        }

        @Test
        @DisplayName("当列表为空时应返回空列表")
        void shouldReturnEmptyListWhenSourceListIsEmpty() {
            // Arrange
            List<User> users = CollUtil.newArrayList();

            // Act
            List<UserVO> result = MapstructUtils.convert(users, UserVO.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("当列表为null时应返回null")
        void shouldReturnNullWhenSourceListIsNull() {
            // Act
            List<UserVO> result = MapstructUtils.convert((List<User>) null, UserVO.class);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该正确转换复杂对象列表")
        void shouldConvertComplexObjectListWhenValidInput() {
            // Arrange
            List<Order> orders = Arrays.asList(
                new Order(100L, "ORDER-001", 1L, new BigDecimal("100.00"), "PAID"),
                new Order(101L, "ORDER-002", 2L, new BigDecimal("200.00"), "PENDING")
            );

            // Act
            List<OrderVO> result = MapstructUtils.convert(orders, OrderVO.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getOrderId()).isEqualTo(100L);
            assertThat(result.get(0).getOrderNo()).isEqualTo("ORDER-001");
            assertThat(result.get(1).getOrderId()).isEqualTo(101L);
            assertThat(result.get(1).getOrderNo()).isEqualTo("ORDER-002");
        }

        @Test
        @DisplayName("应该处理包含单个元素的列表")
        void shouldConvertSingleElementListWhenValidInput() {
            // Arrange
            List<User> users = Arrays.asList(
                new User(1L, "zhangsan", "zhangsan@example.com", 25, LocalDateTime.now())
            );

            // Act
            List<UserVO> result = MapstructUtils.convert(users, UserVO.class);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(1L);
            assertThat(result.get(0).getUsername()).isEqualTo("zhangsan");
        }
    }

    @Nested
    @DisplayName("convert(Map, Class) 方法测试 - Map转Bean")
    class ConvertMapToBeanTest {

        @Test
        @DisplayName("应该抛出异常当没有配置对应的转换器时")
        void shouldThrowExceptionWhenNoConverterConfigured() {
            // Arrange
            Map<String, Object> map = new HashMap<>();
            map.put("id", 1L);
            map.put("username", "zhangsan");
            map.put("email", "zhangsan@example.com");
            map.put("age", 25);

            // Act & Assert
            // MapStruct Plus 需要显式配置 Map 到 Bean 的转换器
            // 由于测试环境未配置，预期会抛出 ConvertException
            assertThatThrownBy(() -> MapstructUtils.convert(map, User.class))
                .isInstanceOf(io.github.linpeilie.ConvertException.class)
                .hasMessageContaining("cannot find converter");
        }

        @Test
        @DisplayName("当Map为null时应返回null")
        void shouldReturnNullWhenMapIsNull() {
            // Act
            User result = MapstructUtils.convert((Map<String, Object>) null, User.class);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("当Map为空时应返回null")
        void shouldReturnNullWhenMapIsEmpty() {
            // Arrange
            Map<String, Object> map = new HashMap<>();

            // Act
            User result = MapstructUtils.convert(map, User.class);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("当目标类为null时应返回null")
        void shouldReturnNullWhenBeanClassIsNull() {
            // Arrange
            Map<String, Object> map = new HashMap<>();
            map.put("id", 1L);
            map.put("username", "zhangsan");

            // Act
            User result = MapstructUtils.convert(map, null);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("复杂对象Map转换也需要显式配置转换器")
        void shouldRequireConverterConfigurationForComplexObjects() {
            // Arrange
            Map<String, Object> map = new HashMap<>();
            map.put("orderId", 100L);
            map.put("orderNo", "ORDER-2024-001");
            map.put("userId", 1L);
            map.put("totalAmount", new BigDecimal("999.99"));
            map.put("status", "PAID");

            // Act & Assert
            // MapStruct Plus 对于复杂对象也需要显式配置转换器
            assertThatThrownBy(() -> MapstructUtils.convert(map, Order.class))
                .isInstanceOf(io.github.linpeilie.ConvertException.class)
                .hasMessageContaining("cannot find converter");
        }
    }
}
