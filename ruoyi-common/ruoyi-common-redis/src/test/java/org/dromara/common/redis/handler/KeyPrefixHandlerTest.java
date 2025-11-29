package org.dromara.common.redis.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * {@link KeyPrefixHandler} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("KeyPrefixHandler 单元测试")
class KeyPrefixHandlerTest {

  @Nested
  @DisplayName("构造函数测试")
  class ConstructorTests {

    @Test
    @DisplayName("应该使用有效的前缀创建处理器")
    void shouldCreateHandlerWithValidPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("myapp");

      String result = handler.map("user:123");

      assertThat(result).isEqualTo("myapp:user:123");
    }

    @Test
    @DisplayName("应该使用null前缀创建空前缀处理器")
    void shouldCreateHandlerWithNullPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler(null);

      String result = handler.map("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("应该使用空字符串前缀创建空前缀处理器")
    void shouldCreateHandlerWithEmptyPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("");

      String result = handler.map("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    @DisplayName("应该使用空白字符前缀创建空前缀处理器")
    void shouldCreateHandlerWithBlankPrefix(String prefix) {
      KeyPrefixHandler handler = new KeyPrefixHandler(prefix);

      String result = handler.map("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("应该自动在前缀后添加冒号")
    void shouldAutomaticallyAddColonAfterPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("app");

      String result = handler.map("key");

      assertThat(result).isEqualTo("app:key");
    }
  }

  @Nested
  @DisplayName("map() 方法测试 - 添加前缀")
  class MapMethodTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("应该为null或空字符串返回null")
    void shouldReturnNullForNullOrEmptyInput(String input) {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.map(input);

      assertThat(result).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "\t", "\n", "   \t\n"})
    @DisplayName("应该为空白字符串返回null")
    void shouldReturnNullForBlankInput(String input) {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.map(input);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("应该为简单key添加前缀")
    void shouldAddPrefixToSimpleKey() {
      KeyPrefixHandler handler = new KeyPrefixHandler("myapp");

      String result = handler.map("user");

      assertThat(result).isEqualTo("myapp:user");
    }

    @Test
    @DisplayName("应该为复杂key添加前缀")
    void shouldAddPrefixToComplexKey() {
      KeyPrefixHandler handler = new KeyPrefixHandler("cache");

      String result = handler.map("user:profile:123");

      assertThat(result).isEqualTo("cache:user:profile:123");
    }

    @Test
    @DisplayName("应该避免重复添加前缀")
    void shouldAvoidDuplicatePrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.map("test:user:123");

      assertThat(result).isEqualTo("test:user:123");
    }

    @Test
    @DisplayName("当前缀为空时应该返回原始key")
    void shouldReturnOriginalKeyWhenPrefixIsEmpty() {
      KeyPrefixHandler handler = new KeyPrefixHandler("");

      String result = handler.map("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("当前缀为null时应该返回原始key")
    void shouldReturnOriginalKeyWhenPrefixIsNull() {
      KeyPrefixHandler handler = new KeyPrefixHandler(null);

      String result = handler.map("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("应该处理包含特殊字符的key")
    void shouldHandleKeyWithSpecialCharacters() {
      KeyPrefixHandler handler = new KeyPrefixHandler("app");

      String result = handler.map("user@email.com");

      assertThat(result).isEqualTo("app:user@email.com");
    }

    @Test
    @DisplayName("应该处理数字key")
    void shouldHandleNumericKey() {
      KeyPrefixHandler handler = new KeyPrefixHandler("seq");

      String result = handler.map("123456");

      assertThat(result).isEqualTo("seq:123456");
    }

    @Test
    @DisplayName("应该处理Unicode字符key")
    void shouldHandleUnicodeKey() {
      KeyPrefixHandler handler = new KeyPrefixHandler("系统");

      String result = handler.map("用户:张三");

      assertThat(result).isEqualTo("系统:用户:张三");
    }
  }

  @Nested
  @DisplayName("unmap() 方法测试 - 移除前缀")
  class UnmapMethodTests {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("应该为null或空字符串返回null")
    void shouldReturnNullForNullOrEmptyInput(String input) {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.unmap(input);

      assertThat(result).isNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "\t", "\n", "   \t\n"})
    @DisplayName("应该为空白字符串返回null")
    void shouldReturnNullForBlankInput(String input) {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.unmap(input);

      assertThat(result).isNull();
    }

    @Test
    @DisplayName("应该正确移除前缀")
    void shouldRemovePrefixCorrectly() {
      KeyPrefixHandler handler = new KeyPrefixHandler("myapp");

      String result = handler.unmap("myapp:user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("应该移除前缀保留剩余部分")
    void shouldRemovePrefixAndKeepRemainder() {
      KeyPrefixHandler handler = new KeyPrefixHandler("cache");

      String result = handler.unmap("cache:product:detail:999");

      assertThat(result).isEqualTo("product:detail:999");
    }

    @Test
    @DisplayName("应该仅移除匹配的前缀")
    void shouldOnlyRemoveMatchingPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.unmap("other:user:123");

      assertThat(result).isEqualTo("other:user:123");
    }

    @Test
    @DisplayName("应该保留不带前缀的key")
    void shouldKeepKeyWithoutPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("app");

      String result = handler.unmap("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("当前缀为空时应该返回原始key")
    void shouldReturnOriginalKeyWhenPrefixIsEmpty() {
      KeyPrefixHandler handler = new KeyPrefixHandler("");

      String result = handler.unmap("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("当前缀为null时应该返回原始key")
    void shouldReturnOriginalKeyWhenPrefixIsNull() {
      KeyPrefixHandler handler = new KeyPrefixHandler(null);

      String result = handler.unmap("user:123");

      assertThat(result).isEqualTo("user:123");
    }

    @Test
    @DisplayName("应该正确移除Unicode前缀")
    void shouldRemoveUnicodePrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("系统");

      String result = handler.unmap("系统:用户:张三");

      assertThat(result).isEqualTo("用户:张三");
    }

    @Test
    @DisplayName("应该处理前缀与key相同的情况")
    void shouldHandleKeyEqualToPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");

      String result = handler.unmap("test:");

      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("边界情况测试")
  class EdgeCaseTests {

    @Test
    @DisplayName("应该处理超长前缀")
    void shouldHandleVeryLongPrefix() {
      String longPrefix = "a".repeat(1000);
      KeyPrefixHandler handler = new KeyPrefixHandler(longPrefix);

      String result = handler.map("key");

      assertThat(result).startsWith(longPrefix + ":");
      assertThat(result).endsWith("key");
    }

    @Test
    @DisplayName("应该处理超长key")
    void shouldHandleVeryLongKey() {
      KeyPrefixHandler handler = new KeyPrefixHandler("app");
      String longKey = "k".repeat(10000);

      String result = handler.map(longKey);

      assertThat(result).isEqualTo("app:" + longKey);
    }

    @Test
    @DisplayName("map和unmap应该是可逆操作")
    void mapAndUnmapShouldBeReversible() {
      KeyPrefixHandler handler = new KeyPrefixHandler("test");
      String originalKey = "user:profile:123";

      String mapped = handler.map(originalKey);
      String unmapped = handler.unmap(mapped);

      assertThat(unmapped).isEqualTo(originalKey);
    }

    @Test
    @DisplayName("多次map不应该重复添加前缀")
    void multipleMapShouldNotAddDuplicatePrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("app");
      String key = "user:123";

      String mapped1 = handler.map(key);
      String mapped2 = handler.map(mapped1);
      String mapped3 = handler.map(mapped2);

      assertThat(mapped1).isEqualTo("app:user:123");
      assertThat(mapped2).isEqualTo("app:user:123");
      assertThat(mapped3).isEqualTo("app:user:123");
    }

    @Test
    @DisplayName("多次unmap不应该重复移除")
    void multipleUnmapShouldNotRemoveTwice() {
      KeyPrefixHandler handler = new KeyPrefixHandler("app");
      String key = "app:user:123";

      String unmapped1 = handler.unmap(key);
      String unmapped2 = handler.unmap(unmapped1);
      String unmapped3 = handler.unmap(unmapped2);

      assertThat(unmapped1).isEqualTo("user:123");
      assertThat(unmapped2).isEqualTo("user:123");
      assertThat(unmapped3).isEqualTo("user:123");
    }
  }

  @Nested
  @DisplayName("业务场景测试")
  class BusinessScenarioTests {

    @Test
    @DisplayName("应该支持用户缓存key的前缀管理")
    void shouldSupportUserCacheKeyPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("user-cache");

      String mappedKey = handler.map("user:123:profile");
      assertThat(mappedKey).isEqualTo("user-cache:user:123:profile");

      String unmappedKey = handler.unmap(mappedKey);
      assertThat(unmappedKey).isEqualTo("user:123:profile");
    }

    @Test
    @DisplayName("应该支持多租户环境的key隔离")
    void shouldSupportMultiTenantKeyIsolation() {
      KeyPrefixHandler tenant1Handler = new KeyPrefixHandler("tenant-001");
      KeyPrefixHandler tenant2Handler = new KeyPrefixHandler("tenant-002");

      String key = "order:12345";

      String tenant1Key = tenant1Handler.map(key);
      String tenant2Key = tenant2Handler.map(key);

      assertThat(tenant1Key).isEqualTo("tenant-001:order:12345");
      assertThat(tenant2Key).isEqualTo("tenant-002:order:12345");
      assertThat(tenant1Key).isNotEqualTo(tenant2Key);
    }

    @Test
    @DisplayName("应该支持分布式锁key的前缀管理")
    void shouldSupportDistributedLockKeyPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("lock");

      String lockKey = handler.map("payment:order:123");

      assertThat(lockKey).isEqualTo("lock:payment:order:123");
    }

    @Test
    @DisplayName("应该支持会话存储key的前缀管理")
    void shouldSupportSessionKeyPrefix() {
      KeyPrefixHandler handler = new KeyPrefixHandler("session");

      String sessionId = "abc123def456";
      String mappedKey = handler.map(sessionId);

      assertThat(mappedKey).isEqualTo("session:abc123def456");
    }

    @Test
    @DisplayName("应该支持不同环境的key隔离")
    void shouldSupportEnvironmentKeyIsolation() {
      KeyPrefixHandler devHandler = new KeyPrefixHandler("dev");
      KeyPrefixHandler prodHandler = new KeyPrefixHandler("prod");

      String key = "config:database";

      assertThat(devHandler.map(key)).isEqualTo("dev:config:database");
      assertThat(prodHandler.map(key)).isEqualTo("prod:config:database");
    }
  }

  @Nested
  @DisplayName("NameMapper 接口实现测试")
  class NameMapperInterfaceTests {

    @Test
    @DisplayName("应该正确实现NameMapper接口的map方法")
    void shouldImplementNameMapperMapMethod() {
      org.redisson.api.NameMapper mapper = new KeyPrefixHandler("test");

      String result = mapper.map("key");

      assertThat(result).isEqualTo("test:key");
    }

    @Test
    @DisplayName("应该正确实现NameMapper接口的unmap方法")
    void shouldImplementNameMapperUnmapMethod() {
      org.redisson.api.NameMapper mapper = new KeyPrefixHandler("test");

      String result = mapper.unmap("test:key");

      assertThat(result).isEqualTo("key");
    }

    @Test
    @DisplayName("应该作为NameMapper使用在Redisson配置中")
    void shouldBeUsableAsNameMapperInRedissonConfig() {
      // 模拟 Redisson 使用场景
      org.redisson.api.NameMapper nameMapper = new KeyPrefixHandler("myapp");

      // Redisson 内部会调用 map 方法来转换 key
      String redisKey = nameMapper.map("user:123");
      assertThat(redisKey).isEqualTo("myapp:user:123");

      // Redisson 内部会调用 unmap 方法来反转换 key
      String originalKey = nameMapper.unmap(redisKey);
      assertThat(originalKey).isEqualTo("user:123");
    }
  }
}
