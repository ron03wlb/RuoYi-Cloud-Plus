package org.dromara.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * SpringBoot-MQ 案例项目
 *
 * @author Lion Li
 */
@SpringBootApplication
public class RuoYiTestMqApplication {

  public static void main(String[] args) {
    // 禁用 Nacos 默认日志配置，避免 Logback appender 冲突
    System.setProperty("nacos.logging.default.config.enabled", "false");

    SpringApplication application = new SpringApplication(RuoYiTestMqApplication.class);
    application.setApplicationStartup(new BufferingApplicationStartup(2048));
    application.run(args);
    System.out.println("(♥◠‿◠)ﾉﾞ  MQ案例模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
