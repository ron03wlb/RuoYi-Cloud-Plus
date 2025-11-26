package org.dromara.common.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * 测试用 Spring Boot 应用
 *
 * <p>最小化配置，仅包含集成测试所需的核心组件：
 *
 * <ul>
 *   <li>MessageSource - 用于国际化消息
 *   <li>Validator - 由 ValidatorConfig 自动配置
 *   <li>SpringUtils - 自动扫描为 @Component
 * </ul>
 *
 * <p>注意：Validator bean 已在 ValidatorConfig 中定义，无需重复配置
 *
 * @author Test Team
 */
@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    /** 配置国际化消息源 用于 MessageUtils 测试 */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18n/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true); // 如果找不到消息，返回 code
        return messageSource;
    }
}
