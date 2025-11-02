package org.dromara.common.core.config;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 测试用 Bean 配置
 *
 * <p>为 SpringUtils 和 MapstructUtils 测试提供必要的 Bean</p>
 *
 * @author Test Team
 */
@Configuration
public class TestBeansConfig {

    /**
     * 单例 Bean (主要Bean)
     */
    @Bean("singletonBean")
    @org.springframework.context.annotation.Primary
    public TestService singletonBean() {
        return new TestService("Singleton Bean");
    }

    /**
     * 原型 Bean
     */
    @Bean("prototypeBean")
    @Scope("prototype")
    public TestService prototypeBean() {
        return new TestService("Prototype Bean");
    }

    /**
     * 带别名的 Bean
     */
    @Bean(name = {"primaryName", "alias1", "alias2"})
    public TestService aliasedBean() {
        return new TestService("Aliased Bean");
    }

    /**
     * AOP 代理 Bean
     */
    @Bean("proxyBean")
    public TestService proxyBean() {
        TestService target = new TestService("Proxy Bean");
        ProxyFactory factory = new ProxyFactory(target);
        factory.setProxyTargetClass(true);
        return (TestService) factory.getProxy();
    }

    /**
     * 测试服务类
     */
    public static class TestService {
        private final String name;

        public TestService(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
