package org.dromara.modules.monitor;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 监控中心
 *
 * @author ruoyi
 */
@EnableAdminServer
@SpringBootApplication
public class RuoYiMonitorApplication {
  public static void main(String[] args) {
    // 禁用 Nacos 默认日志配置，避免 Logback appender 冲突
    System.setProperty("nacos.logging.default.config.enabled", "false");

    SpringApplication.run(RuoYiMonitorApplication.class, args);
    System.out.println("(♥◠‿◠)ﾉﾞ  监控中心启动成功   ლ(´ڡ`ლ)ﾞ  ");
  }
}
