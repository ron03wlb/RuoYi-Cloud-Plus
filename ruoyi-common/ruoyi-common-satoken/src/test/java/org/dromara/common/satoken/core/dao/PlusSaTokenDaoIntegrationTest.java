package org.dromara.common.satoken.core.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.dromara.common.satoken.BaseSaTokenIntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * PlusSaTokenDao 集成測試
 *
 * <p>測試 Sa-Token DAO 層的 Redis + Caffeine 多級緩存功能
 *
 * <p>測試場景:
 *
 * <ul>
 *   <li>字符串值的存取和刪除
 *   <li>對象值的存取和刪除
 *   <li>超時時間的獲取和更新
 *   <li>值的更新操作
 *   <li>數據搜索功能
 * </ul>
 *
 * @author Test Team
 */
@SpringBootTest
@DisplayName("PlusSaTokenDao 集成測試")
class PlusSaTokenDaoIntegrationTest extends BaseSaTokenIntegrationTest {

    @Autowired private PlusSaTokenDao saTokenDao;

    /** 測試 Key 前綴 */
    private static final String TEST_KEY_PREFIX = "test:satoken:";

    /** 每個測試後清理測試數據 */
    @AfterEach
    void cleanup() {
        // 清理可能殘留的測試數據
        saTokenDao.delete(TEST_KEY_PREFIX + "value");
        saTokenDao.deleteObject(TEST_KEY_PREFIX + "object");
    }

    /** 測試字符串值操作 */
    @Nested
    @DisplayName("字符串值操作測試")
    class StringValueTests {

        @Test
        @DisplayName("應該成功存取字符串值")
        void shouldSetAndGetStringValue() {
            // Arrange
            String key = TEST_KEY_PREFIX + "value";
            String value = "test-value-123";
            long timeout = 60; // 60 秒

            // Act
            saTokenDao.set(key, value, timeout);
            String retrievedValue = saTokenDao.get(key);

            // Assert
            assertThat(retrievedValue).isEqualTo(value);
        }

        @Test
        @DisplayName("應該在未設置時返回 null")
        void shouldReturnNullForNonExistentKey() {
            // Arrange
            String key = TEST_KEY_PREFIX + "nonexistent";

            // Act
            String value = saTokenDao.get(key);

            // Assert
            assertThat(value).isNull();
        }

        @Test
        @DisplayName("應該成功刪除字符串值")
        void shouldDeleteStringValue() {
            // Arrange
            String key = TEST_KEY_PREFIX + "value";
            String value = "test-value";
            saTokenDao.set(key, value, 60);
            assertThat(saTokenDao.get(key)).isEqualTo(value);

            // Act
            saTokenDao.delete(key);

            // Assert
            assertThat(saTokenDao.get(key)).isNull();
        }

        @Test
        @DisplayName("應該支持永不過期的值")
        void shouldSupportNeverExpireValue() {
            // Arrange
            String key = TEST_KEY_PREFIX + "never-expire";
            String value = "永不過期的值";

            // Act
            saTokenDao.set(key, value, -1); // NEVER_EXPIRE = -1
            String retrievedValue = saTokenDao.get(key);

            // Assert
            assertThat(retrievedValue).isEqualTo(value);

            // Cleanup
            saTokenDao.delete(key);
        }

        @Test
        @DisplayName("應該更新已存在的值")
        void shouldUpdateExistingValue() {
            // Arrange
            String key = TEST_KEY_PREFIX + "update";
            saTokenDao.set(key, "old-value", 60);

            // Act
            saTokenDao.update(key, "new-value");
            String updatedValue = saTokenDao.get(key);

            // Assert
            assertThat(updatedValue).isEqualTo("new-value");

            // Cleanup
            saTokenDao.delete(key);
        }
    }

    /** 測試對象值操作 */
    @Nested
    @DisplayName("對象值操作測試")
    class ObjectValueTests {

        @Test
        @DisplayName("應該成功存取對象值")
        void shouldSetAndGetObjectValue() {
            // Arrange
            String key = TEST_KEY_PREFIX + "object";
            TestObject object = new TestObject("測試對象", 123);
            long timeout = 60;

            // Act
            saTokenDao.setObject(key, object, timeout);
            TestObject retrievedObject = saTokenDao.getObject(key, TestObject.class);

            // Assert
            assertThat(retrievedObject).isNotNull();
            assertThat(retrievedObject.getName()).isEqualTo("測試對象");
            assertThat(retrievedObject.getValue()).isEqualTo(123);
        }

        @Test
        @DisplayName("應該成功刪除對象值")
        void shouldDeleteObjectValue() {
            // Arrange
            String key = TEST_KEY_PREFIX + "object";
            TestObject object = new TestObject("test", 456);
            saTokenDao.setObject(key, object, 60);
            assertThat(saTokenDao.getObject(key)).isNotNull();

            // Act
            saTokenDao.deleteObject(key);

            // Assert
            assertThat(saTokenDao.getObject(key)).isNull();
        }

        @Test
        @DisplayName("應該更新已存在的對象")
        void shouldUpdateExistingObject() {
            // Arrange
            String key = TEST_KEY_PREFIX + "update-obj";
            TestObject oldObject = new TestObject("old", 1);
            TestObject newObject = new TestObject("new", 2);
            saTokenDao.setObject(key, oldObject, 60);

            // Act
            saTokenDao.updateObject(key, newObject);
            TestObject updated = saTokenDao.getObject(key, TestObject.class);

            // Assert
            assertThat(updated).isNotNull();
            assertThat(updated.getName()).isEqualTo("new");
            assertThat(updated.getValue()).isEqualTo(2);

            // Cleanup
            saTokenDao.deleteObject(key);
        }
    }

    /** 測試超時操作 */
    @Nested
    @DisplayName("超時操作測試")
    class TimeoutTests {

        @Test
        @DisplayName("應該正確獲取剩餘超時時間")
        void shouldGetCorrectTimeout() {
            // Arrange
            String key = TEST_KEY_PREFIX + "timeout-test";
            long expectedTimeout = 120; // 2 分鐘
            saTokenDao.set(key, "test", expectedTimeout);

            // Act
            long actualTimeout = saTokenDao.getTimeout(key);

            // Assert
            // 允許 ±5 秒的誤差（因為有執行時間）
            assertThat(actualTimeout).isBetween(expectedTimeout - 5, expectedTimeout + 1);

            // Cleanup
            saTokenDao.delete(key);
        }

        @Test
        @DisplayName("應該正確更新超時時間")
        void shouldUpdateTimeout() {
            // Arrange
            String key = TEST_KEY_PREFIX + "update-timeout";
            saTokenDao.set(key, "test", 60);

            // Act
            saTokenDao.updateTimeout(key, 300); // 更新為 5 分鐘
            long newTimeout = saTokenDao.getTimeout(key);

            // Assert
            assertThat(newTimeout).isGreaterThan(250); // 至少還有 4 分鐘多

            // Cleanup
            saTokenDao.delete(key);
        }

        @Test
        @DisplayName("應該正確獲取對象的超時時間")
        void shouldGetObjectTimeout() {
            // Arrange
            String key = TEST_KEY_PREFIX + "obj-timeout";
            TestObject object = new TestObject("test", 1);
            long expectedTimeout = 180; // 3 分鐘
            saTokenDao.setObject(key, object, expectedTimeout);

            // Act
            long actualTimeout = saTokenDao.getObjectTimeout(key);

            // Assert
            assertThat(actualTimeout).isBetween(expectedTimeout - 5, expectedTimeout + 1);

            // Cleanup
            saTokenDao.deleteObject(key);
        }

        @Test
        @DisplayName("應該正確更新對象的超時時間")
        void shouldUpdateObjectTimeout() {
            // Arrange
            String key = TEST_KEY_PREFIX + "update-obj-timeout";
            TestObject object = new TestObject("test", 1);
            saTokenDao.setObject(key, object, 60);

            // Act
            saTokenDao.updateObjectTimeout(key, 240); // 更新為 4 分鐘
            long newTimeout = saTokenDao.getObjectTimeout(key);

            // Assert
            assertThat(newTimeout).isGreaterThan(200); // 至少還有 3 分鐘多

            // Cleanup
            saTokenDao.deleteObject(key);
        }
    }

    /** 測試搜索功能 */
    @Nested
    @DisplayName("數據搜索測試")
    class SearchDataTests {

        @Test
        @DisplayName("應該能搜索匹配的 Key")
        void shouldSearchMatchingKeys() {
            // Arrange
            String prefix = TEST_KEY_PREFIX + "search:";
            saTokenDao.set(prefix + "user:1", "data1", 60);
            saTokenDao.set(prefix + "user:2", "data2", 60);
            saTokenDao.set(prefix + "user:3", "data3", 60);
            saTokenDao.set(prefix + "admin:1", "admin-data", 60);

            try {
                // Act
                List<String> results = saTokenDao.searchData(prefix, "user", 0, 10, true);

                // Assert
                assertThat(results).isNotEmpty();
                assertThat(results.size()).isLessThanOrEqualTo(3);

            } finally {
                // Cleanup
                saTokenDao.delete(prefix + "user:1");
                saTokenDao.delete(prefix + "user:2");
                saTokenDao.delete(prefix + "user:3");
                saTokenDao.delete(prefix + "admin:1");
            }
        }

        @Test
        @DisplayName("應該在無匹配時返回空列表")
        void shouldReturnEmptyListWhenNoMatch() {
            // Act
            List<String> results =
                    saTokenDao.searchData(TEST_KEY_PREFIX, "nonexistent-key-xyz", 0, 10, true);

            // Assert
            assertThat(results).isNotNull();
            assertThat(results).isEmpty();
        }
    }

    /** 測試用對象類 */
    @org.springframework.stereotype.Component
    static class TestObject implements java.io.Serializable {
        private String name;
        private Integer value;

        public TestObject() {}

        public TestObject(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
