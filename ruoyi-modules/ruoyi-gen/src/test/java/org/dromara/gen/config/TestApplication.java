package org.dromara.gen.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 测试专用应用配置类
 *
 * <p>不包含 @EnableDubbo 注解，避免在测试环境中初始化 Dubbo
 *
 * @author Lion Li
 * @since 2025-11-10
 */
@SpringBootApplication(
    scanBasePackages = {"org.dromara.gen"},
    excludeName = {
      "org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration",
      "org.apache.dubbo.spring.boot.autoconfigure.DubboRelaxedBindingAutoConfiguration",
      "org.dromara.common.dubbo.config.DubboConfiguration"
    })
public class TestApplication {
  // 仅用于测试配置，不需要 main 方法
}
