package org.dromara.common.redis.config.properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.config.ReadMode;
import org.redisson.config.SubscriptionMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * {@link RedissonProperties} 单元测试
 *
 * @author Lion Li
 */
@DisplayName("RedissonProperties 单元测试")
class RedissonPropertiesTest {

  @Nested
  @DisplayName("RedissonProperties 主类测试")
  class MainPropertiesTests {

    @Test
    @DisplayName("应该能够创建默认实例")
    void shouldCreateDefaultInstance() {
      RedissonProperties properties = new RedissonProperties();

      assertThat(properties).isNotNull();
    }

    @Test
    @DisplayName("应该能够设置和获取keyPrefix")
    void shouldSetAndGetKeyPrefix() {
      RedissonProperties properties = new RedissonProperties();

      properties.setKeyPrefix("myapp");

      assertThat(properties.getKeyPrefix()).isEqualTo("myapp");
    }

    @Test
    @DisplayName("应该能够设置和获取threads")
    void shouldSetAndGetThreads() {
      RedissonProperties properties = new RedissonProperties();

      properties.setThreads(16);

      assertThat(properties.getThreads()).isEqualTo(16);
    }

    @Test
    @DisplayName("应该能够设置和获取nettyThreads")
    void shouldSetAndGetNettyThreads() {
      RedissonProperties properties = new RedissonProperties();

      properties.setNettyThreads(32);

      assertThat(properties.getNettyThreads()).isEqualTo(32);
    }

    @Test
    @DisplayName("应该能够设置和获取singleServerConfig")
    void shouldSetAndGetSingleServerConfig() {
      RedissonProperties properties = new RedissonProperties();
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      properties.setSingleServerConfig(config);

      assertThat(properties.getSingleServerConfig()).isSameAs(config);
    }

    @Test
    @DisplayName("应该能够设置和获取clusterServersConfig")
    void shouldSetAndGetClusterServersConfig() {
      RedissonProperties properties = new RedissonProperties();
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      properties.setClusterServersConfig(config);

      assertThat(properties.getClusterServersConfig()).isSameAs(config);
    }

    @Test
    @DisplayName("应该支持链式调用")
    void shouldSupportFluentSetters() {
      RedissonProperties properties = new RedissonProperties();

      properties.setKeyPrefix("test");
      properties.setThreads(8);
      properties.setNettyThreads(16);

      assertThat(properties.getKeyPrefix()).isEqualTo("test");
      assertThat(properties.getThreads()).isEqualTo(8);
      assertThat(properties.getNettyThreads()).isEqualTo(16);
    }
  }

  @Nested
  @DisplayName("SingleServerConfig 测试")
  class SingleServerConfigTests {

    @Test
    @DisplayName("应该能够创建默认实例")
    void shouldCreateDefaultInstance() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("应该能够设置和获取clientName")
    void shouldSetAndGetClientName() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setClientName("my-redis-client");

      assertThat(config.getClientName()).isEqualTo("my-redis-client");
    }

    @Test
    @DisplayName("应该能够设置和获取connectionMinimumIdleSize")
    void shouldSetAndGetConnectionMinimumIdleSize() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setConnectionMinimumIdleSize(10);

      assertThat(config.getConnectionMinimumIdleSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("应该能够设置和获取connectionPoolSize")
    void shouldSetAndGetConnectionPoolSize() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setConnectionPoolSize(64);

      assertThat(config.getConnectionPoolSize()).isEqualTo(64);
    }

    @Test
    @DisplayName("应该能够设置和获取idleConnectionTimeout")
    void shouldSetAndGetIdleConnectionTimeout() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setIdleConnectionTimeout(10000);

      assertThat(config.getIdleConnectionTimeout()).isEqualTo(10000);
    }

    @Test
    @DisplayName("应该能够设置和获取timeout")
    void shouldSetAndGetTimeout() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setTimeout(3000);

      assertThat(config.getTimeout()).isEqualTo(3000);
    }

    @Test
    @DisplayName("应该能够设置和获取subscriptionConnectionPoolSize")
    void shouldSetAndGetSubscriptionConnectionPoolSize() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setSubscriptionConnectionPoolSize(50);

      assertThat(config.getSubscriptionConnectionPoolSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("应该支持完整配置")
    void shouldSupportCompleteConfiguration() {
      RedissonProperties.SingleServerConfig config = new RedissonProperties.SingleServerConfig();

      config.setClientName("test-client");
      config.setConnectionMinimumIdleSize(5);
      config.setConnectionPoolSize(32);
      config.setIdleConnectionTimeout(20000);
      config.setTimeout(5000);
      config.setSubscriptionConnectionPoolSize(25);

      assertThat(config.getClientName()).isEqualTo("test-client");
      assertThat(config.getConnectionMinimumIdleSize()).isEqualTo(5);
      assertThat(config.getConnectionPoolSize()).isEqualTo(32);
      assertThat(config.getIdleConnectionTimeout()).isEqualTo(20000);
      assertThat(config.getTimeout()).isEqualTo(5000);
      assertThat(config.getSubscriptionConnectionPoolSize()).isEqualTo(25);
    }
  }

  @Nested
  @DisplayName("ClusterServersConfig 测试")
  class ClusterServersConfigTests {

    @Test
    @DisplayName("应该能够创建默认实例")
    void shouldCreateDefaultInstance() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      assertThat(config).isNotNull();
    }

    @Test
    @DisplayName("应该能够设置和获取clientName")
    void shouldSetAndGetClientName() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setClientName("cluster-client");

      assertThat(config.getClientName()).isEqualTo("cluster-client");
    }

    @Test
    @DisplayName("应该能够设置和获取masterConnectionMinimumIdleSize")
    void shouldSetAndGetMasterConnectionMinimumIdleSize() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setMasterConnectionMinimumIdleSize(10);

      assertThat(config.getMasterConnectionMinimumIdleSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("应该能够设置和获取masterConnectionPoolSize")
    void shouldSetAndGetMasterConnectionPoolSize() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setMasterConnectionPoolSize(64);

      assertThat(config.getMasterConnectionPoolSize()).isEqualTo(64);
    }

    @Test
    @DisplayName("应该能够设置和获取slaveConnectionMinimumIdleSize")
    void shouldSetAndGetSlaveConnectionMinimumIdleSize() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setSlaveConnectionMinimumIdleSize(10);

      assertThat(config.getSlaveConnectionMinimumIdleSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("应该能够设置和获取slaveConnectionPoolSize")
    void shouldSetAndGetSlaveConnectionPoolSize() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setSlaveConnectionPoolSize(64);

      assertThat(config.getSlaveConnectionPoolSize()).isEqualTo(64);
    }

    @Test
    @DisplayName("应该能够设置和获取idleConnectionTimeout")
    void shouldSetAndGetIdleConnectionTimeout() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setIdleConnectionTimeout(10000);

      assertThat(config.getIdleConnectionTimeout()).isEqualTo(10000);
    }

    @Test
    @DisplayName("应该能够设置和获取timeout")
    void shouldSetAndGetTimeout() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setTimeout(3000);

      assertThat(config.getTimeout()).isEqualTo(3000);
    }

    @Test
    @DisplayName("应该能够设置和获取subscriptionConnectionPoolSize")
    void shouldSetAndGetSubscriptionConnectionPoolSize() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setSubscriptionConnectionPoolSize(50);

      assertThat(config.getSubscriptionConnectionPoolSize()).isEqualTo(50);
    }

    @Test
    @DisplayName("应该能够设置和获取readMode")
    void shouldSetAndGetReadMode() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setReadMode(ReadMode.SLAVE);

      assertThat(config.getReadMode()).isEqualTo(ReadMode.SLAVE);
    }

    @Test
    @DisplayName("应该能够设置和获取subscriptionMode")
    void shouldSetAndGetSubscriptionMode() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setSubscriptionMode(SubscriptionMode.MASTER);

      assertThat(config.getSubscriptionMode()).isEqualTo(SubscriptionMode.MASTER);
    }

    @Test
    @DisplayName("应该支持所有ReadMode枚举值")
    void shouldSupportAllReadModeValues() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      for (ReadMode mode : ReadMode.values()) {
        config.setReadMode(mode);
        assertThat(config.getReadMode()).isEqualTo(mode);
      }
    }

    @Test
    @DisplayName("应该支持所有SubscriptionMode枚举值")
    void shouldSupportAllSubscriptionModeValues() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      for (SubscriptionMode mode : SubscriptionMode.values()) {
        config.setSubscriptionMode(mode);
        assertThat(config.getSubscriptionMode()).isEqualTo(mode);
      }
    }

    @Test
    @DisplayName("应该支持完整的集群配置")
    void shouldSupportCompleteClusterConfiguration() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      config.setClientName("cluster-client");
      config.setMasterConnectionMinimumIdleSize(5);
      config.setMasterConnectionPoolSize(32);
      config.setSlaveConnectionMinimumIdleSize(8);
      config.setSlaveConnectionPoolSize(64);
      config.setIdleConnectionTimeout(20000);
      config.setTimeout(5000);
      config.setSubscriptionConnectionPoolSize(25);
      config.setReadMode(ReadMode.SLAVE);
      config.setSubscriptionMode(SubscriptionMode.MASTER);

      assertThat(config.getClientName()).isEqualTo("cluster-client");
      assertThat(config.getMasterConnectionMinimumIdleSize()).isEqualTo(5);
      assertThat(config.getMasterConnectionPoolSize()).isEqualTo(32);
      assertThat(config.getSlaveConnectionMinimumIdleSize()).isEqualTo(8);
      assertThat(config.getSlaveConnectionPoolSize()).isEqualTo(64);
      assertThat(config.getIdleConnectionTimeout()).isEqualTo(20000);
      assertThat(config.getTimeout()).isEqualTo(5000);
      assertThat(config.getSubscriptionConnectionPoolSize()).isEqualTo(25);
      assertThat(config.getReadMode()).isEqualTo(ReadMode.SLAVE);
      assertThat(config.getSubscriptionMode()).isEqualTo(SubscriptionMode.MASTER);
    }
  }

  @Nested
  @DisplayName("注解验证测试")
  class AnnotationTests {

    @Test
    @DisplayName("RedissonProperties应该有@ConfigurationProperties注解")
    void shouldHaveConfigurationPropertiesAnnotation() {
      ConfigurationProperties annotation =
          RedissonProperties.class.getAnnotation(ConfigurationProperties.class);

      assertThat(annotation).isNotNull();
      assertThat(annotation.prefix()).isEqualTo("redisson");
    }

    @Test
    @DisplayName("RedissonProperties应该有无参构造函数")
    void shouldHaveNoArgsConstructor() {
      assertThatNoException().isThrownBy(RedissonProperties::new);
    }

    @Test
    @DisplayName("SingleServerConfig应该有无参构造函数")
    void singleServerConfigShouldHaveNoArgsConstructor() {
      assertThatNoException().isThrownBy(RedissonProperties.SingleServerConfig::new);
    }

    @Test
    @DisplayName("ClusterServersConfig应该有无参构造函数")
    void clusterServersConfigShouldHaveNoArgsConstructor() {
      assertThatNoException().isThrownBy(RedissonProperties.ClusterServersConfig::new);
    }

    @Test
    @DisplayName("RedissonProperties应该有所有字段的getter和setter方法")
    void shouldHaveGettersAndSettersForAllFields() throws Exception {
      Class<RedissonProperties> clazz = RedissonProperties.class;

      // 验证 keyPrefix
      Method getKeyPrefix = clazz.getMethod("getKeyPrefix");
      Method setKeyPrefix = clazz.getMethod("setKeyPrefix", String.class);
      assertThat(getKeyPrefix).isNotNull();
      assertThat(setKeyPrefix).isNotNull();

      // 验证 threads
      Method getThreads = clazz.getMethod("getThreads");
      Method setThreads = clazz.getMethod("setThreads", int.class);
      assertThat(getThreads).isNotNull();
      assertThat(setThreads).isNotNull();

      // 验证 nettyThreads
      Method getNettyThreads = clazz.getMethod("getNettyThreads");
      Method setNettyThreads = clazz.getMethod("setNettyThreads", int.class);
      assertThat(getNettyThreads).isNotNull();
      assertThat(setNettyThreads).isNotNull();
    }

    @Test
    @DisplayName("SingleServerConfig应该有所有字段的getter和setter方法")
    void singleServerConfigShouldHaveGettersAndSettersForAllFields() throws Exception {
      Class<RedissonProperties.SingleServerConfig> clazz =
          RedissonProperties.SingleServerConfig.class;

      assertThat(clazz.getMethod("getClientName")).isNotNull();
      assertThat(clazz.getMethod("setClientName", String.class)).isNotNull();
      assertThat(clazz.getMethod("getConnectionMinimumIdleSize")).isNotNull();
      assertThat(clazz.getMethod("setConnectionMinimumIdleSize", int.class)).isNotNull();
      assertThat(clazz.getMethod("getConnectionPoolSize")).isNotNull();
      assertThat(clazz.getMethod("setConnectionPoolSize", int.class)).isNotNull();
      assertThat(clazz.getMethod("getIdleConnectionTimeout")).isNotNull();
      assertThat(clazz.getMethod("setIdleConnectionTimeout", int.class)).isNotNull();
      assertThat(clazz.getMethod("getTimeout")).isNotNull();
      assertThat(clazz.getMethod("setTimeout", int.class)).isNotNull();
      assertThat(clazz.getMethod("getSubscriptionConnectionPoolSize")).isNotNull();
      assertThat(clazz.getMethod("setSubscriptionConnectionPoolSize", int.class)).isNotNull();
    }

    @Test
    @DisplayName("ClusterServersConfig应该有所有字段的getter和setter方法")
    void clusterServersConfigShouldHaveGettersAndSettersForAllFields() throws Exception {
      Class<RedissonProperties.ClusterServersConfig> clazz =
          RedissonProperties.ClusterServersConfig.class;

      assertThat(clazz.getMethod("getClientName")).isNotNull();
      assertThat(clazz.getMethod("setClientName", String.class)).isNotNull();
      assertThat(clazz.getMethod("getMasterConnectionMinimumIdleSize")).isNotNull();
      assertThat(clazz.getMethod("setMasterConnectionMinimumIdleSize", int.class)).isNotNull();
      assertThat(clazz.getMethod("getMasterConnectionPoolSize")).isNotNull();
      assertThat(clazz.getMethod("setMasterConnectionPoolSize", int.class)).isNotNull();
      assertThat(clazz.getMethod("getSlaveConnectionMinimumIdleSize")).isNotNull();
      assertThat(clazz.getMethod("setSlaveConnectionMinimumIdleSize", int.class)).isNotNull();
      assertThat(clazz.getMethod("getSlaveConnectionPoolSize")).isNotNull();
      assertThat(clazz.getMethod("setSlaveConnectionPoolSize", int.class)).isNotNull();
      assertThat(clazz.getMethod("getReadMode")).isNotNull();
      assertThat(clazz.getMethod("setReadMode", ReadMode.class)).isNotNull();
      assertThat(clazz.getMethod("getSubscriptionMode")).isNotNull();
      assertThat(clazz.getMethod("setSubscriptionMode", SubscriptionMode.class)).isNotNull();
    }
  }

  @Nested
  @DisplayName("业务场景测试")
  class BusinessScenarioTests {

    @Test
    @DisplayName("应该支持单机Redis配置场景")
    void shouldSupportSingleServerScenario() {
      RedissonProperties properties = new RedissonProperties();
      properties.setKeyPrefix("myapp");
      properties.setThreads(16);
      properties.setNettyThreads(32);

      RedissonProperties.SingleServerConfig singleConfig =
          new RedissonProperties.SingleServerConfig();
      singleConfig.setClientName("single-client");
      singleConfig.setConnectionMinimumIdleSize(10);
      singleConfig.setConnectionPoolSize(64);
      singleConfig.setIdleConnectionTimeout(10000);
      singleConfig.setTimeout(3000);
      singleConfig.setSubscriptionConnectionPoolSize(50);

      properties.setSingleServerConfig(singleConfig);

      assertThat(properties.getKeyPrefix()).isEqualTo("myapp");
      assertThat(properties.getSingleServerConfig()).isNotNull();
      assertThat(properties.getSingleServerConfig().getClientName()).isEqualTo("single-client");
      assertThat(properties.getClusterServersConfig()).isNull();
    }

    @Test
    @DisplayName("应该支持集群Redis配置场景")
    void shouldSupportClusterScenario() {
      RedissonProperties properties = new RedissonProperties();
      properties.setKeyPrefix("cluster-app");
      properties.setThreads(32);
      properties.setNettyThreads(64);

      RedissonProperties.ClusterServersConfig clusterConfig =
          new RedissonProperties.ClusterServersConfig();
      clusterConfig.setClientName("cluster-client");
      clusterConfig.setMasterConnectionMinimumIdleSize(10);
      clusterConfig.setMasterConnectionPoolSize(64);
      clusterConfig.setSlaveConnectionMinimumIdleSize(20);
      clusterConfig.setSlaveConnectionPoolSize(128);
      clusterConfig.setReadMode(ReadMode.SLAVE);
      clusterConfig.setSubscriptionMode(SubscriptionMode.MASTER);

      properties.setClusterServersConfig(clusterConfig);

      assertThat(properties.getKeyPrefix()).isEqualTo("cluster-app");
      assertThat(properties.getClusterServersConfig()).isNotNull();
      assertThat(properties.getClusterServersConfig().getReadMode()).isEqualTo(ReadMode.SLAVE);
      assertThat(properties.getSingleServerConfig()).isNull();
    }

    @Test
    @DisplayName("应该支持高性能配置")
    void shouldSupportHighPerformanceConfiguration() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      // 高性能配置
      config.setMasterConnectionPoolSize(128);
      config.setSlaveConnectionPoolSize(256);
      config.setMasterConnectionMinimumIdleSize(32);
      config.setSlaveConnectionMinimumIdleSize(64);
      config.setReadMode(ReadMode.SLAVE); // 读从库，减轻主库压力
      config.setTimeout(1000); // 短超时

      assertThat(config.getMasterConnectionPoolSize()).isEqualTo(128);
      assertThat(config.getSlaveConnectionPoolSize()).isEqualTo(256);
      assertThat(config.getReadMode()).isEqualTo(ReadMode.SLAVE);
    }

    @Test
    @DisplayName("应该支持不同环境的配置")
    void shouldSupportDifferentEnvironmentConfigurations() {
      // 开发环境配置
      RedissonProperties devProps = new RedissonProperties();
      devProps.setKeyPrefix("dev");
      devProps.setThreads(8);

      // 生产环境配置
      RedissonProperties prodProps = new RedissonProperties();
      prodProps.setKeyPrefix("prod");
      prodProps.setThreads(32);

      assertThat(devProps.getKeyPrefix()).isEqualTo("dev");
      assertThat(prodProps.getKeyPrefix()).isEqualTo("prod");
      assertThat(devProps.getThreads()).isLessThan(prodProps.getThreads());
    }

    @Test
    @DisplayName("应该支持主从读写分离配置")
    void shouldSupportMasterSlaveReadWriteSeparation() {
      RedissonProperties.ClusterServersConfig config =
          new RedissonProperties.ClusterServersConfig();

      // 主库用于写，从库用于读
      config.setReadMode(ReadMode.SLAVE);
      config.setSubscriptionMode(SubscriptionMode.MASTER);
      config.setMasterConnectionPoolSize(32);
      config.setSlaveConnectionPoolSize(64); // 读多于写

      assertThat(config.getReadMode()).isEqualTo(ReadMode.SLAVE);
      assertThat(config.getSubscriptionMode()).isEqualTo(SubscriptionMode.MASTER);
      assertThat(config.getSlaveConnectionPoolSize())
          .isGreaterThan(config.getMasterConnectionPoolSize());
    }
  }

  @Nested
  @DisplayName("对象相等性测试")
  class EqualityTests {

    @Test
    @DisplayName("相同配置的对象应该相等")
    void objectsWithSameConfigurationShouldBeEqual() {
      RedissonProperties props1 = new RedissonProperties();
      props1.setKeyPrefix("test");
      props1.setThreads(16);

      RedissonProperties props2 = new RedissonProperties();
      props2.setKeyPrefix("test");
      props2.setThreads(16);

      assertThat(props1).isEqualTo(props2);
    }

    @Test
    @DisplayName("SingleServerConfig对象应该支持equals")
    void singleServerConfigShouldSupportEquals() {
      RedissonProperties.SingleServerConfig config1 = new RedissonProperties.SingleServerConfig();
      config1.setClientName("test");
      config1.setConnectionPoolSize(64);

      RedissonProperties.SingleServerConfig config2 = new RedissonProperties.SingleServerConfig();
      config2.setClientName("test");
      config2.setConnectionPoolSize(64);

      assertThat(config1).isEqualTo(config2);
    }

    @Test
    @DisplayName("ClusterServersConfig对象应该支持equals")
    void clusterServersConfigShouldSupportEquals() {
      RedissonProperties.ClusterServersConfig config1 =
          new RedissonProperties.ClusterServersConfig();
      config1.setClientName("cluster");
      config1.setReadMode(ReadMode.SLAVE);

      RedissonProperties.ClusterServersConfig config2 =
          new RedissonProperties.ClusterServersConfig();
      config2.setClientName("cluster");
      config2.setReadMode(ReadMode.SLAVE);

      assertThat(config1).isEqualTo(config2);
    }
  }

  @Nested
  @DisplayName("字段验证测试")
  class FieldValidationTests {

    @Test
    @DisplayName("RedissonProperties应该有keyPrefix字段")
    void shouldHaveKeyPrefixField() throws NoSuchFieldException {
      Field field = RedissonProperties.class.getDeclaredField("keyPrefix");

      assertThat(field).isNotNull();
      assertThat(field.getType()).isEqualTo(String.class);
    }

    @Test
    @DisplayName("RedissonProperties应该有threads字段")
    void shouldHaveThreadsField() throws NoSuchFieldException {
      Field field = RedissonProperties.class.getDeclaredField("threads");

      assertThat(field).isNotNull();
      assertThat(field.getType()).isEqualTo(int.class);
    }

    @Test
    @DisplayName("ClusterServersConfig应该有readMode字段")
    void shouldHaveReadModeField() throws NoSuchFieldException {
      Field field = RedissonProperties.ClusterServersConfig.class.getDeclaredField("readMode");

      assertThat(field).isNotNull();
      assertThat(field.getType()).isEqualTo(ReadMode.class);
    }

    @Test
    @DisplayName("ClusterServersConfig应该有subscriptionMode字段")
    void shouldHaveSubscriptionModeField() throws NoSuchFieldException {
      Field field =
          RedissonProperties.ClusterServersConfig.class.getDeclaredField("subscriptionMode");

      assertThat(field).isNotNull();
      assertThat(field.getType()).isEqualTo(SubscriptionMode.class);
    }
  }

  @Nested
  @DisplayName("嵌套类测试")
  class NestedClassTests {

    @Test
    @DisplayName("SingleServerConfig应该是静态内部类")
    void singleServerConfigShouldBeStaticInnerClass() throws NoSuchFieldException {
      Class<?> enclosingClass = RedissonProperties.SingleServerConfig.class.getEnclosingClass();

      assertThat(enclosingClass).isEqualTo(RedissonProperties.class);
      assertThat(
              java.lang.reflect.Modifier.isStatic(
                  RedissonProperties.SingleServerConfig.class.getModifiers()))
          .isTrue();
    }

    @Test
    @DisplayName("ClusterServersConfig应该是静态内部类")
    void clusterServersConfigShouldBeStaticInnerClass() {
      Class<?> enclosingClass = RedissonProperties.ClusterServersConfig.class.getEnclosingClass();

      assertThat(enclosingClass).isEqualTo(RedissonProperties.class);
      assertThat(
              java.lang.reflect.Modifier.isStatic(
                  RedissonProperties.ClusterServersConfig.class.getModifiers()))
          .isTrue();
    }

    @Test
    @DisplayName("嵌套类应该可以独立实例化")
    void nestedClassesShouldBeInstantiableIndependently() {
      RedissonProperties.SingleServerConfig singleConfig =
          new RedissonProperties.SingleServerConfig();
      RedissonProperties.ClusterServersConfig clusterConfig =
          new RedissonProperties.ClusterServersConfig();

      assertThat(singleConfig).isNotNull();
      assertThat(clusterConfig).isNotNull();
    }
  }
}
