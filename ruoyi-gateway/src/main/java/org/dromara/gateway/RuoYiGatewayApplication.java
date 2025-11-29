package org.dromara.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 网关启动程序.
 *
 * @author ruoyi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiGatewayApplication {
  /**
   * 主函数，应用程序入口.
   *
   * @param args 命令行参数
   */
  public static void main(String[] args) {
    // 禁用 Nacos 默认日志配置，避免 Logback appender 冲突
    System.setProperty("nacos.logging.default.config.enabled", "false");

    SpringApplication application = new SpringApplication(RuoYiGatewayApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  网关启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
