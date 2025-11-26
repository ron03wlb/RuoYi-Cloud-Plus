package org.dromara.common.core.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dromara.common.core.BaseIntegrationTest;
import org.dromara.common.core.config.TestBeansConfig.TestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * SpringUtils 集成测试
 *
 * @author Test Team
 */
@DisplayName("SpringUtils 集成测试")
class SpringUtilsIntegrationTest extends BaseIntegrationTest {

    @Nested
    @DisplayName("containsBean 方法测试")
    class ContainsBeanTest {

        @Test
        @DisplayName("当Bean存在时应返回true")
        void shouldReturnTrueWhenBeanExists() {
            // Act & Assert
            assertThat(SpringUtils.containsBean("singletonBean")).isTrue();
            assertThat(SpringUtils.containsBean("prototypeBean")).isTrue();
            assertThat(SpringUtils.containsBean("primaryName")).isTrue();
        }

        @Test
        @DisplayName("当Bean不存在时应返回false")
        void shouldReturnFalseWhenBeanNotExists() {
            // Act & Assert
            assertThat(SpringUtils.containsBean("nonExistentBean")).isFalse();
            assertThat(SpringUtils.containsBean("invalidBeanName")).isFalse();
        }

        @Test
        @DisplayName("应该识别Bean的别名")
        void shouldRecognizeBeanAliasWhenCheckingBean() {
            // Act & Assert
            assertThat(SpringUtils.containsBean("alias1")).isTrue();
            assertThat(SpringUtils.containsBean("alias2")).isTrue();
        }

        @Test
        @DisplayName("应该识别Spring内置Bean")
        void shouldRecognizeSpringInternalBeansWhenCheckingBean() {
            // Act & Assert
            assertThat(SpringUtils.containsBean("environment")).isTrue();
            assertThat(SpringUtils.containsBean("messageSource")).isTrue();
        }
    }

    @Nested
    @DisplayName("isSingleton 方法测试")
    class IsSingletonTest {

        @Test
        @DisplayName("当Bean是单例时应返回true")
        void shouldReturnTrueWhenBeanIsSingleton() {
            // Act & Assert
            assertThat(SpringUtils.isSingleton("singletonBean")).isTrue();
            assertThat(SpringUtils.isSingleton("primaryName")).isTrue();
        }

        @Test
        @DisplayName("当Bean是原型时应返回false")
        void shouldReturnFalseWhenBeanIsPrototype() {
            // Act & Assert
            assertThat(SpringUtils.isSingleton("prototypeBean")).isFalse();
        }

        @Test
        @DisplayName("当Bean不存在时应抛出异常")
        void shouldThrowExceptionWhenBeanNotExists() {
            // Act & Assert
            assertThatThrownBy(() -> SpringUtils.isSingleton("nonExistentBean"))
                    .isInstanceOf(NoSuchBeanDefinitionException.class)
                    .hasMessageContaining("nonExistentBean");
        }

        @Test
        @DisplayName("应该正确识别Spring内置Bean为单例")
        void shouldReturnTrueForSpringInternalSingletonBeans() {
            // Act & Assert
            assertThat(SpringUtils.isSingleton("environment")).isTrue();
            assertThat(SpringUtils.isSingleton("messageSource")).isTrue();
        }
    }

    @Nested
    @DisplayName("getType 方法测试")
    class GetTypeTest {

        @Test
        @DisplayName("应该返回正确的Bean类型")
        void shouldReturnCorrectTypeWhenBeanExists() {
            // Act
            Class<?> type = SpringUtils.getType("singletonBean");

            // Assert
            assertThat(type).isEqualTo(TestService.class);
        }

        @Test
        @DisplayName("应该返回原型Bean的类型")
        void shouldReturnPrototypeBeanTypeWhenBeanExists() {
            // Act
            Class<?> type = SpringUtils.getType("prototypeBean");

            // Assert
            assertThat(type).isEqualTo(TestService.class);
        }

        @Test
        @DisplayName("当Bean不存在时应抛出异常")
        void shouldThrowExceptionWhenBeanNotExists() {
            // Act & Assert
            assertThatThrownBy(() -> SpringUtils.getType("nonExistentBean"))
                    .isInstanceOf(NoSuchBeanDefinitionException.class)
                    .hasMessageContaining("nonExistentBean");
        }

        @Test
        @DisplayName("应该返回Spring内置Bean的类型")
        void shouldReturnCorrectTypeForSpringInternalBeans() {
            // Act
            Class<?> envType = SpringUtils.getType("environment");

            // Assert
            assertThat(Environment.class).isAssignableFrom(envType);
        }

        @Test
        @DisplayName("应该通过别名获取Bean类型")
        void shouldReturnCorrectTypeWhenUsingAlias() {
            // Act
            Class<?> type1 = SpringUtils.getType("primaryName");
            Class<?> type2 = SpringUtils.getType("alias1");
            Class<?> type3 = SpringUtils.getType("alias2");

            // Assert
            assertThat(type1).isEqualTo(TestService.class);
            assertThat(type2).isEqualTo(TestService.class);
            assertThat(type3).isEqualTo(TestService.class);
        }
    }

    @Nested
    @DisplayName("getAliases 方法测试")
    class GetAliasesTest {

        @Test
        @DisplayName("应该返回Bean的所有别名")
        void shouldReturnAllAliasesWhenBeanHasAliases() {
            // Act
            String[] aliases = SpringUtils.getAliases("primaryName");

            // Assert
            assertThat(aliases).isNotNull();
            assertThat(aliases).containsExactlyInAnyOrder("alias1", "alias2");
        }

        @Test
        @DisplayName("当Bean没有别名时应返回空数组")
        void shouldReturnEmptyArrayWhenBeanHasNoAliases() {
            // Act
            String[] aliases = SpringUtils.getAliases("singletonBean");

            // Assert
            assertThat(aliases).isNotNull();
            assertThat(aliases).isEmpty();
        }

        @Test
        @DisplayName("当Bean不存在时应返回空数组")
        void shouldReturnEmptyArrayWhenBeanNotExists() {
            // Act
            String[] aliases = SpringUtils.getAliases("nonExistentBean");

            // Assert
            assertThat(aliases).isNotNull();
            assertThat(aliases).isEmpty();
        }

        @Test
        @DisplayName("应该通过别名查询到主Bean名称")
        void shouldReturnPrimaryNameWhenQueryingWithAlias() {
            // Act
            String[] aliasesFromAlias1 = SpringUtils.getAliases("alias1");

            // Assert
            assertThat(aliasesFromAlias1).contains("primaryName");
        }
    }

    @Nested
    @DisplayName("getAopProxy 方法测试")
    class GetAopProxyTest {

        @Test
        @DisplayName("应该返回AOP代理对象")
        void shouldReturnAopProxyWhenInvokerProvided() {
            // Arrange
            TestService original = SpringUtils.getBean("singletonBean", TestService.class);

            // Act
            TestService proxy = SpringUtils.getAopProxy(original);

            // Assert
            assertThat(proxy).isNotNull();
            assertThat(proxy).isInstanceOf(TestService.class);
            assertThat(proxy.getName()).isEqualTo("Singleton Bean");
        }

        @Test
        @DisplayName("应该返回与原对象相同类型的代理")
        void shouldReturnProxyOfSameTypeWhenInvokerProvided() {
            // Arrange
            TestService original = SpringUtils.getBean("proxyBean", TestService.class);

            // Act
            TestService proxy = SpringUtils.getAopProxy(original);

            // Assert
            assertThat(proxy).isNotNull();
            assertThat(proxy.getClass()).isEqualTo(original.getClass());
        }

        @Test
        @DisplayName("应该处理已经是代理的对象")
        void shouldHandleAlreadyProxiedObjectWhenInvokerProvided() {
            // Arrange
            TestService alreadyProxied = SpringUtils.getBean("proxyBean", TestService.class);

            // Act
            TestService proxy = SpringUtils.getAopProxy(alreadyProxied);

            // Assert
            assertThat(proxy).isNotNull();
            assertThat(proxy.getName()).isEqualTo("Proxy Bean");
        }
    }

    @Nested
    @DisplayName("context 方法测试")
    class ContextTest {

        @Test
        @DisplayName("应该返回ApplicationContext实例")
        void shouldReturnApplicationContextWhenInvoked() {
            // Act
            ApplicationContext context = SpringUtils.context();

            // Assert
            assertThat(context).isNotNull();
            assertThat(context).isInstanceOf(ApplicationContext.class);
        }

        @Test
        @DisplayName("应该返回包含测试Bean的上下文")
        void shouldReturnContextWithTestBeansWhenInvoked() {
            // Act
            ApplicationContext context = SpringUtils.context();

            // Assert
            assertThat(context.containsBean("singletonBean")).isTrue();
            assertThat(context.containsBean("prototypeBean")).isTrue();
            assertThat(context.containsBean("primaryName")).isTrue();
        }

        @Test
        @DisplayName("多次调用应返回相同的ApplicationContext实例")
        void shouldReturnSameInstanceWhenInvokedMultipleTimes() {
            // Act
            ApplicationContext context1 = SpringUtils.context();
            ApplicationContext context2 = SpringUtils.context();

            // Assert
            assertThat(context1).isSameAs(context2);
        }

        @Test
        @DisplayName("应该能够通过context直接获取Bean")
        void shouldAllowDirectBeanRetrievalFromContextWhenInvoked() {
            // Act
            ApplicationContext context = SpringUtils.context();
            TestService bean = context.getBean("singletonBean", TestService.class);

            // Assert
            assertThat(bean).isNotNull();
            assertThat(bean.getName()).isEqualTo("Singleton Bean");
        }
    }

    @Nested
    @DisplayName("isVirtual 方法测试")
    class IsVirtualTest {

        @Test
        @DisplayName("应该返回虚拟线程状态")
        void shouldReturnVirtualThreadStatusWhenInvoked() {
            // Act
            boolean isVirtual = SpringUtils.isVirtual();

            // Assert
            // 在测试环境中，通常不使用虚拟线程，所以应该返回 false
            assertThat(isVirtual).isFalse();
        }

        @Test
        @DisplayName("应该能够获取Environment Bean")
        void shouldAccessEnvironmentBeanWhenCheckingVirtualThreads() {
            // Act
            Environment env = SpringUtils.getBean(Environment.class);

            // Assert
            assertThat(env).isNotNull();
            assertThat(env).isInstanceOf(Environment.class);
        }
    }

    @Nested
    @DisplayName("继承自SpringUtil的方法测试")
    class InheritedMethodsTest {

        @Test
        @DisplayName("应该能够通过类型获取Bean")
        void shouldGetBeanByTypeWhenBeanExists() {
            // Act
            TestService bean = SpringUtils.getBean(TestService.class);

            // Assert
            assertThat(bean).isNotNull();
            assertThat(bean).isInstanceOf(TestService.class);
        }

        @Test
        @DisplayName("应该能够通过名称获取Bean")
        void shouldGetBeanByNameWhenBeanExists() {
            // Act
            Object bean = SpringUtils.getBean("singletonBean");

            // Assert
            assertThat(bean).isNotNull();
            assertThat(bean).isInstanceOf(TestService.class);
            assertThat(((TestService) bean).getName()).isEqualTo("Singleton Bean");
        }

        @Test
        @DisplayName("应该能够通过名称和类型获取Bean")
        void shouldGetBeanByNameAndTypeWhenBeanExists() {
            // Act
            TestService bean = SpringUtils.getBean("singletonBean", TestService.class);

            // Assert
            assertThat(bean).isNotNull();
            assertThat(bean.getName()).isEqualTo("Singleton Bean");
        }

        @Test
        @DisplayName("原型Bean每次获取应返回新实例")
        void shouldReturnNewInstanceEachTimeWhenPrototypeBeanRetrieved() {
            // Act
            TestService bean1 = SpringUtils.getBean("prototypeBean", TestService.class);
            TestService bean2 = SpringUtils.getBean("prototypeBean", TestService.class);

            // Assert
            assertThat(bean1).isNotNull();
            assertThat(bean2).isNotNull();
            assertThat(bean1).isNotSameAs(bean2); // 不同实例
            assertThat(bean1.getName()).isEqualTo(bean2.getName()); // 但属性相同
        }

        @Test
        @DisplayName("单例Bean每次获取应返回相同实例")
        void shouldReturnSameInstanceEachTimeWhenSingletonBeanRetrieved() {
            // Act
            TestService bean1 = SpringUtils.getBean("singletonBean", TestService.class);
            TestService bean2 = SpringUtils.getBean("singletonBean", TestService.class);

            // Assert
            assertThat(bean1).isNotNull();
            assertThat(bean2).isNotNull();
            assertThat(bean1).isSameAs(bean2); // 相同实例
        }
    }
}
