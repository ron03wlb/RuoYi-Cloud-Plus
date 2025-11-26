package org.dromara.common.core.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ObjectUtils 单元测试
 *
 * @author Lion Li
 */
@DisplayName("ObjectUtils 工具类测试")
class ObjectUtilsTest extends BaseUnitTest {

    @Nested
    @DisplayName("notNullGetter 方法测试")
    class NotNullGetterTest {

        @Test
        @DisplayName("对象为null时返回null")
        void shouldReturnNullWhenObjectIsNull() {
            TestUser user = null;
            String result = ObjectUtils.notNullGetter(user, TestUser::getName);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("对象不为null但函数为null时返回null")
        void shouldReturnNullWhenFunctionIsNull() {
            TestUser user = new TestUser("张三", 25);
            String result = ObjectUtils.notNullGetter(user, null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("对象和函数都不为null时返回字段值")
        void shouldReturnFieldValueWhenObjectAndFunctionAreNotNull() {
            TestUser user = new TestUser("张三", 25);
            String result = ObjectUtils.notNullGetter(user, TestUser::getName);
            assertThat(result).isEqualTo("张三");
        }

        @Test
        @DisplayName("获取Integer字段")
        void shouldReturnIntegerField() {
            TestUser user = new TestUser("张三", 25);
            Integer result = ObjectUtils.notNullGetter(user, TestUser::getAge);
            assertThat(result).isEqualTo(25);
        }

        @Test
        @DisplayName("获取的字段值本身为null")
        void shouldReturnNullWhenFieldValueIsNull() {
            TestUser user = new TestUser(null, 25);
            String result = ObjectUtils.notNullGetter(user, TestUser::getName);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("链式调用获取嵌套字段")
        void shouldSupportChainedCalls() {
            TestUser user = new TestUser("张三", 25);
            user.setAddress(new TestAddress("北京", "海淀区"));

            TestAddress address = ObjectUtils.notNullGetter(user, TestUser::getAddress);
            String result = ObjectUtils.notNullGetter(address, TestAddress::getCity);
            assertThat(result).isEqualTo("北京");
        }
    }

    @Nested
    @DisplayName("notNullGetter 带默认值方法测试")
    class NotNullGetterWithDefaultTest {

        @Test
        @DisplayName("对象为null时返回默认值")
        void shouldReturnDefaultValueWhenObjectIsNull() {
            TestUser user = null;
            String result = ObjectUtils.notNullGetter(user, TestUser::getName, "默认名称");
            assertThat(result).isEqualTo("默认名称");
        }

        @Test
        @DisplayName("函数为null时返回默认值")
        void shouldReturnDefaultValueWhenFunctionIsNull() {
            TestUser user = new TestUser("张三", 25);
            String result = ObjectUtils.notNullGetter(user, null, "默认名称");
            assertThat(result).isEqualTo("默认名称");
        }

        @Test
        @DisplayName("对象和函数都不为null时返回字段值而非默认值")
        void shouldReturnFieldValueNotDefaultWhenObjectAndFunctionAreNotNull() {
            TestUser user = new TestUser("张三", 25);
            String result = ObjectUtils.notNullGetter(user, TestUser::getName, "默认名称");
            assertThat(result).isEqualTo("张三");
        }

        @Test
        @DisplayName("字段值为null时仍返回null而非默认值")
        void shouldReturnNullNotDefaultWhenFieldValueIsNull() {
            TestUser user = new TestUser(null, 25);
            String result = ObjectUtils.notNullGetter(user, TestUser::getName, "默认名称");
            // 注意：字段值为null时，会返回null，而不是defaultValue
            // defaultValue只在对象本身或函数为null时使用
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("默认值为null")
        void shouldSupportNullDefaultValue() {
            TestUser user = null;
            String result = ObjectUtils.notNullGetter(user, TestUser::getName, null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("默认值为复杂对象")
        void shouldSupportComplexDefaultValue() {
            TestUser user = null;
            TestAddress defaultAddress = new TestAddress("默认城市", "默认区域");
            TestAddress result =
                    ObjectUtils.notNullGetter(user, TestUser::getAddress, defaultAddress);
            assertThat(result).isEqualTo(defaultAddress);
            assertThat(result.getCity()).isEqualTo("默认城市");
        }
    }

    @Nested
    @DisplayName("notNull 方法测试")
    class NotNullTest {

        @Test
        @DisplayName("对象为null时返回默认值")
        void shouldReturnDefaultValueWhenObjectIsNull() {
            String value = null;
            String result = ObjectUtils.notNull(value, "默认值");
            assertThat(result).isEqualTo("默认值");
        }

        @Test
        @DisplayName("对象不为null时返回对象本身")
        void shouldReturnObjectWhenObjectIsNotNull() {
            String value = "实际值";
            String result = ObjectUtils.notNull(value, "默认值");
            assertThat(result).isEqualTo("实际值");
        }

        @Test
        @DisplayName("默认值为null")
        void shouldSupportNullDefaultValue() {
            String value = null;
            String result = ObjectUtils.notNull(value, null);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("对象和默认值都不为null")
        void shouldReturnObjectWhenBothAreNotNull() {
            String value = "实际值";
            String result = ObjectUtils.notNull(value, "默认值");
            assertThat(result).isEqualTo("实际值");
        }

        @Test
        @DisplayName("使用Integer类型")
        void shouldWorkWithIntegerType() {
            Integer value = null;
            Integer result = ObjectUtils.notNull(value, 100);
            assertThat(result).isEqualTo(100);
        }

        @Test
        @DisplayName("使用Boolean类型")
        void shouldWorkWithBooleanType() {
            Boolean value = null;
            Boolean result = ObjectUtils.notNull(value, false);
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("使用复杂对象")
        void shouldWorkWithComplexObjects() {
            TestUser user = null;
            TestUser defaultUser = new TestUser("默认用户", 0);
            TestUser result = ObjectUtils.notNull(user, defaultUser);
            assertThat(result).isEqualTo(defaultUser);
            assertThat(result.getName()).isEqualTo("默认用户");
        }

        @Test
        @DisplayName("连续调用")
        void shouldSupportChainedCalls() {
            String value = null;
            String result = ObjectUtils.notNull(ObjectUtils.notNull(value, "第一默认值"), "第二默认值");
            assertThat(result).isEqualTo("第一默认值");
        }
    }

    @Nested
    @DisplayName("继承自Hutool的方法测试")
    class HutoolMethodsTest {

        @Test
        @DisplayName("isNull方法")
        void shouldTestIsNull() {
            assertThat(ObjectUtils.isNull(null)).isTrue();
            assertThat(ObjectUtils.isNull("")).isFalse();
            assertThat(ObjectUtils.isNull("value")).isFalse();
        }

        @Test
        @DisplayName("isNotNull方法")
        void shouldTestIsNotNull() {
            assertThat(ObjectUtils.isNotNull(null)).isFalse();
            assertThat(ObjectUtils.isNotNull("")).isTrue();
            assertThat(ObjectUtils.isNotNull("value")).isTrue();
        }

        @Test
        @DisplayName("isEmpty方法")
        void shouldTestIsEmpty() {
            assertThat(ObjectUtils.isEmpty(null)).isTrue();
            assertThat(ObjectUtils.isEmpty("")).isTrue();
            assertThat(ObjectUtils.isEmpty("  ")).isFalse();
            assertThat(ObjectUtils.isEmpty("value")).isFalse();
        }

        @Test
        @DisplayName("isNotEmpty方法")
        void shouldTestIsNotEmpty() {
            assertThat(ObjectUtils.isNotEmpty(null)).isFalse();
            assertThat(ObjectUtils.isNotEmpty("")).isFalse();
            assertThat(ObjectUtils.isNotEmpty("  ")).isTrue();
            assertThat(ObjectUtils.isNotEmpty("value")).isTrue();
        }

        @Test
        @DisplayName("defaultIfNull方法")
        void shouldTestDefaultIfNull() {
            assertThat(ObjectUtils.defaultIfNull(null, "default")).isEqualTo("default");
            assertThat(ObjectUtils.defaultIfNull("value", "default")).isEqualTo("value");
        }

        @Test
        @DisplayName("equal方法")
        void shouldTestEqual() {
            assertThat(ObjectUtils.equal(null, null)).isTrue();
            assertThat(ObjectUtils.equal("value", "value")).isTrue();
            assertThat(ObjectUtils.equal("value1", "value2")).isFalse();
            assertThat(ObjectUtils.equal(null, "value")).isFalse();
        }

        @Test
        @DisplayName("notEqual方法")
        void shouldTestNotEqual() {
            assertThat(ObjectUtils.notEqual(null, null)).isFalse();
            assertThat(ObjectUtils.notEqual("value", "value")).isFalse();
            assertThat(ObjectUtils.notEqual("value1", "value2")).isTrue();
            assertThat(ObjectUtils.notEqual(null, "value")).isTrue();
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCasesTest {

        @Test
        @DisplayName("空字符串作为默认值")
        void shouldSupportEmptyStringAsDefault() {
            String value = null;
            String result = ObjectUtils.notNull(value, "");
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("零值作为默认值")
        void shouldSupportZeroAsDefault() {
            Integer value = null;
            Integer result = ObjectUtils.notNull(value, 0);
            assertThat(result).isZero();
        }

        @Test
        @DisplayName("Lambda表达式获取多层嵌套字段")
        void shouldSupportNestedFieldAccess() {
            TestUser user = new TestUser("张三", 25);
            user.setAddress(new TestAddress("北京", "海淀区"));

            TestAddress address = ObjectUtils.notNullGetter(user, TestUser::getAddress);
            assertThat(address).isNotNull();
            assertThat(address.getCity()).isEqualTo("北京");
        }

        @Test
        @DisplayName("方法引用获取字段")
        void shouldSupportMethodReference() {
            TestUser user = new TestUser("张三", 25);
            // 使用方法引用
            String name = ObjectUtils.notNullGetter(user, TestUser::getName);
            assertThat(name).isEqualTo("张三");

            // 使用Lambda
            String nameLambda = ObjectUtils.notNullGetter(user, u -> u.getName());
            assertThat(nameLambda).isEqualTo("张三");
        }
    }

    // ========== 测试辅助类 ==========

    /** 测试用户类 */
    private static class TestUser {
        private String name;
        private Integer age;
        private TestAddress address;

        public TestUser(String name, Integer age) {
            this.name = name;
            this.age = age;
        }

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

        public TestAddress getAddress() {
            return address;
        }

        public void setAddress(TestAddress address) {
            this.address = address;
        }
    }

    /** 测试地址类 */
    private static class TestAddress {
        private String city;
        private String district;

        public TestAddress(String city, String district) {
            this.city = city;
            this.district = district;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }
    }
}
