package org.dromara.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 演示模块
 *
 * @author Lion Li
 */
@SpringBootApplication
public class RuoYiDemoApplication {
    public static void main(String[] args) {
        // 禁用 Nacos 默认日志配置，避免 Logback appender 冲突
        System.setProperty("nacos.logging.default.config.enabled", "false");

        SpringApplication application = new SpringApplication(RuoYiDemoApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  演示模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
