package org.dromara.auth;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * Authentication and authorization center main application.
 *
 * @author ruoyi
 */
@EnableDubbo
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiAuthApplication {

  /**
   * Main entry point for the authentication and authorization center application.
   *
   * @param args command-line arguments
   */
  public static void main(String[] args) {
    // 禁用 Nacos 默认日志配置，避免 Logback appender 冲突
    System.setProperty("nacos.logging.default.config.enabled", "false");

    SpringApplication application = new SpringApplication(RuoYiAuthApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  认证授权中心启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
