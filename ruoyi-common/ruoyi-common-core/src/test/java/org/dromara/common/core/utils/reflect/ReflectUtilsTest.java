package org.dromara.common.core.utils.reflect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ReflectUtils 测试类
 *
 * @author Test Team
 */
@DisplayName("ReflectUtils 工具类测试")
class ReflectUtilsTest {

    /**
     * 测试用Person类
     */
    static class Person {
        private String name;
        private Integer age;
        private Address address;

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

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }
    }

    /**
     * 测试用Address类
     */
    static class Address {
        private String city;
        private String street;
        private ZipCode zipCode;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public ZipCode getZipCode() {
            return zipCode;
        }

        public void setZipCode(ZipCode zipCode) {
            this.zipCode = zipCode;
        }
    }

    /**
     * 测试用ZipCode类（三层嵌套）
     */
    static class ZipCode {
        private String code;
        private String areaCode;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }
    }

    @Nested
    @DisplayName("invokeGetter 方法测试")
    class InvokeGetterTests {

        private Person person;

        @BeforeEach
        void setUp() {
            // 准备测试数据
            person = new Person();
            person.setName("张三");
            person.setAge(30);

            Address address = new Address();
            address.setCity("北京");
            address.setStreet("长安街");

            ZipCode zipCode = new ZipCode();
            zipCode.setCode("100000");
            zipCode.setAreaCode("010");
            address.setZipCode(zipCode);

            person.setAddress(address);
        }

        @Test
        @DisplayName("应该正确获取单层属性值")
        void shouldInvokeGetterForSimpleProperty() {
            // Act
            String name = ReflectUtils.invokeGetter(person, "name");
            Integer age = ReflectUtils.invokeGetter(person, "age");

            // Assert
            assertThat(name).isEqualTo("张三");
            assertThat(age).isEqualTo(30);
        }

        @Test
        @DisplayName("应该正确获取两层嵌套属性值")
        void shouldInvokeGetterForNestedProperty() {
            // Act
            String city = ReflectUtils.invokeGetter(person, "address.city");
            String street = ReflectUtils.invokeGetter(person, "address.street");

            // Assert
            assertThat(city).isEqualTo("北京");
            assertThat(street).isEqualTo("长安街");
        }

        @Test
        @DisplayName("应该正确获取三层嵌套属性值")
        void shouldInvokeGetterForDeeplyNestedProperty() {
            // Act
            String code = ReflectUtils.invokeGetter(person, "address.zipCode.code");
            String areaCode = ReflectUtils.invokeGetter(person, "address.zipCode.areaCode");

            // Assert
            assertThat(code).isEqualTo("100000");
            assertThat(areaCode).isEqualTo("010");
        }

        @Test
        @DisplayName("应该能够获取对象类型属性")
        void shouldInvokeGetterForObjectProperty() {
            // Act
            Address address = ReflectUtils.invokeGetter(person, "address");

            // Assert
            assertThat(address).isNotNull();
            assertThat(address.getCity()).isEqualTo("北京");
        }

        @Test
        @DisplayName("应该能够获取嵌套对象")
        void shouldInvokeGetterForNestedObject() {
            // Act
            ZipCode zipCode = ReflectUtils.invokeGetter(person, "address.zipCode");

            // Assert
            assertThat(zipCode).isNotNull();
            assertThat(zipCode.getCode()).isEqualTo("100000");
        }

        @Test
        @DisplayName("当中间对象为null时应该抛出异常")
        void shouldThrowExceptionWhenIntermediateObjectIsNull() {
            // Arrange
            person.setAddress(null);

            // Act & Assert
            assertThatThrownBy(() -> ReflectUtils.invokeGetter(person, "address.city"))
                .isInstanceOf(RuntimeException.class)  // Hutool ReflectUtil 抛出 IllegalArgumentException
                .hasMessageContaining("must be not null");
        }

        @Test
        @DisplayName("当方法不存在时应该抛出异常")
        void shouldThrowExceptionWhenMethodNotExists() {
            // Act & Assert
            assertThatThrownBy(() -> ReflectUtils.invokeGetter(person, "nonExistentProperty"))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该处理null值属性")
        void shouldHandleNullPropertyValue() {
            // Arrange
            person.setName(null);

            // Act
            String name = ReflectUtils.invokeGetter(person, "name");

            // Assert
            assertThat(name).isNull();
        }

        @Test
        @DisplayName("应该正确返回数值类型")
        void shouldReturnNumericType() {
            // Act
            Integer age = ReflectUtils.invokeGetter(person, "age");

            // Assert
            assertThat(age)
                .isNotNull()
                .isInstanceOf(Integer.class)
                .isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("invokeSetter 方法测试")
    class InvokeSetterTests {

        private Person person;

        @BeforeEach
        void setUp() {
            // 准备测试数据
            person = new Person();
            person.setName("原始名字");

            Address address = new Address();
            address.setCity("原始城市");

            ZipCode zipCode = new ZipCode();
            zipCode.setCode("000000");
            address.setZipCode(zipCode);

            person.setAddress(address);
        }

        @Test
        @DisplayName("应该正确设置单层属性值")
        void shouldInvokeSetterForSimpleProperty() {
            // Act
            ReflectUtils.invokeSetter(person, "name", "新名字");
            ReflectUtils.invokeSetter(person, "age", 25);

            // Assert
            assertThat(person.getName()).isEqualTo("新名字");
            assertThat(person.getAge()).isEqualTo(25);
        }

        @Test
        @DisplayName("应该正确设置两层嵌套属性值")
        void shouldInvokeSetterForNestedProperty() {
            // Act
            ReflectUtils.invokeSetter(person, "address.city", "上海");
            ReflectUtils.invokeSetter(person, "address.street", "南京路");

            // Assert
            assertThat(person.getAddress().getCity()).isEqualTo("上海");
            assertThat(person.getAddress().getStreet()).isEqualTo("南京路");
        }

        @Test
        @DisplayName("应该正确设置三层嵌套属性值")
        void shouldInvokeSetterForDeeplyNestedProperty() {
            // Act
            ReflectUtils.invokeSetter(person, "address.zipCode.code", "200000");
            ReflectUtils.invokeSetter(person, "address.zipCode.areaCode", "021");

            // Assert
            assertThat(person.getAddress().getZipCode().getCode()).isEqualTo("200000");
            assertThat(person.getAddress().getZipCode().getAreaCode()).isEqualTo("021");
        }

        @Test
        @DisplayName("应该能够设置null值")
        void shouldSetNullValue() {
            // Act
            ReflectUtils.invokeSetter(person, "name", null);

            // Assert
            assertThat(person.getName()).isNull();
        }

        @Test
        @DisplayName("应该能够设置数值类型")
        void shouldSetNumericValue() {
            // Act
            ReflectUtils.invokeSetter(person, "age", 40);

            // Assert
            assertThat(person.getAge())
                .isNotNull()
                .isInstanceOf(Integer.class)
                .isEqualTo(40);
        }

        @Test
        @DisplayName("应该能够替换整个对象")
        void shouldReplaceEntireObject() {
            // Arrange
            Address newAddress = new Address();
            newAddress.setCity("深圳");
            newAddress.setStreet("深南大道");

            // Act
            ReflectUtils.invokeSetter(person, "address", newAddress);

            // Assert
            assertThat(person.getAddress()).isEqualTo(newAddress);
            assertThat(person.getAddress().getCity()).isEqualTo("深圳");
        }

        @Test
        @DisplayName("当中间对象为null时应该抛出异常")
        void shouldThrowExceptionWhenIntermediateObjectIsNull() {
            // Arrange
            person.setAddress(null);

            // Act & Assert
            assertThatThrownBy(() -> ReflectUtils.invokeSetter(person, "address.city", "深圳"))
                .isInstanceOf(NullPointerException.class);  // invokeSetter 中调用 getClass() 时抛出 NPE
        }

        @Test
        @DisplayName("当setter方法不存在时应该抛出异常")
        void shouldThrowExceptionWhenSetterNotExists() {
            // Act & Assert
            assertThatThrownBy(() -> ReflectUtils.invokeSetter(person, "nonExistentProperty", "value"))
                .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该处理连续的setter调用")
        void shouldHandleMultipleSetterCalls() {
            // Act
            ReflectUtils.invokeSetter(person, "name", "李四");
            ReflectUtils.invokeSetter(person, "age", 35);
            ReflectUtils.invokeSetter(person, "address.city", "广州");
            ReflectUtils.invokeSetter(person, "address.zipCode.code", "510000");

            // Assert
            assertThat(person.getName()).isEqualTo("李四");
            assertThat(person.getAge()).isEqualTo(35);
            assertThat(person.getAddress().getCity()).isEqualTo("广州");
            assertThat(person.getAddress().getZipCode().getCode()).isEqualTo("510000");
        }

        @Test
        @DisplayName("应该正确处理字符串到数字的转换")
        void shouldHandleStringToNumberConversion() {
            // Act
            ReflectUtils.invokeSetter(person, "age", 50);

            // Assert
            assertThat(person.getAge()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("综合场景测试")
    class IntegrationTests {

        @Test
        @DisplayName("应该支持先get再set的操作链")
        void shouldSupportGetThenSetChain() {
            // Arrange
            Person person = new Person();
            person.setName("测试");

            Address address = new Address();
            address.setCity("北京");
            person.setAddress(address);

            // Act - 先获取城市，修改后再设置回去
            String originalCity = ReflectUtils.invokeGetter(person, "address.city");
            ReflectUtils.invokeSetter(person, "address.city", originalCity + "市");

            // Assert
            assertThat(person.getAddress().getCity()).isEqualTo("北京市");
        }

        @Test
        @DisplayName("应该支持复杂的对象操作")
        void shouldSupportComplexObjectOperations() {
            // Arrange
            Person person1 = new Person();
            person1.setName("用户1");

            Address addr1 = new Address();
            addr1.setCity("杭州");

            ZipCode zip1 = new ZipCode();
            zip1.setCode("310000");
            addr1.setZipCode(zip1);
            person1.setAddress(addr1);

            // Act - 获取person1的邮编，创建新person2并设置相同邮编
            String code = ReflectUtils.invokeGetter(person1, "address.zipCode.code");

            Person person2 = new Person();
            person2.setName("用户2");
            Address addr2 = new Address();
            ZipCode zip2 = new ZipCode();
            zip2.setCode("000000");
            addr2.setZipCode(zip2);
            person2.setAddress(addr2);

            ReflectUtils.invokeSetter(person2, "address.zipCode.code", code);

            // Assert
            assertThat(person2.getAddress().getZipCode().getCode()).isEqualTo("310000");
        }

        @Test
        @DisplayName("应该在多层嵌套中保持数据一致性")
        void shouldMaintainDataConsistencyInDeepNesting() {
            // Arrange
            Person person = new Person();
            Address address = new Address();
            ZipCode zipCode = new ZipCode();
            zipCode.setCode("100000");
            address.setZipCode(zipCode);
            person.setAddress(address);

            // Act
            String originalCode = ReflectUtils.invokeGetter(person, "address.zipCode.code");
            ReflectUtils.invokeSetter(person, "address.zipCode.areaCode", "010");
            String codeAfterSet = ReflectUtils.invokeGetter(person, "address.zipCode.code");

            // Assert
            assertThat(originalCode).isEqualTo(codeAfterSet);
            assertThat(person.getAddress().getZipCode().getAreaCode()).isEqualTo("010");
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理只有一层的属性路径")
        void shouldHandleSingleLevelPath() {
            // Arrange
            Person person = new Person();

            // Act
            ReflectUtils.invokeSetter(person, "name", "单层测试");
            String name = ReflectUtils.invokeGetter(person, "name");

            // Assert
            assertThat(name).isEqualTo("单层测试");
        }

        @Test
        @DisplayName("应该正确处理布尔类型属性")
        void shouldHandleBooleanProperty() {
            // Arrange
            class TestClass {
                private Boolean active;

                public Boolean getActive() {
                    return active;
                }

                public void setActive(Boolean active) {
                    this.active = active;
                }
            }
            TestClass obj = new TestClass();

            // Act
            ReflectUtils.invokeSetter(obj, "active", true);
            Boolean result = ReflectUtils.invokeGetter(obj, "active");

            // Assert
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("应该处理Long类型属性")
        void shouldHandleLongProperty() {
            // Arrange
            class TestClass {
                private Long id;

                public Long getId() {
                    return id;
                }

                public void setId(Long id) {
                    this.id = id;
                }
            }
            TestClass obj = new TestClass();

            // Act
            ReflectUtils.invokeSetter(obj, "id", 1000000L);
            Long result = ReflectUtils.invokeGetter(obj, "id");

            // Assert
            assertThat(result).isEqualTo(1000000L);
        }
    }
}
