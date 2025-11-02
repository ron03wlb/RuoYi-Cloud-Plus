package org.dromara.common.core;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类
 *
 * <p>用于测试需要 Spring 容器的类，如：</p>
 * <ul>
 *   <li>MessageUtils - 需要 MessageSource 和 SpringUtils</li>
 *   <li>ValidatorUtils - 需要 Validator 和 SpringUtils</li>
 *   <li>BaseException - 需要 MessageUtils</li>
 *   <li>UserException/FileException - 继承自 BaseException</li>
 * </ul>
 *
 * <p>特点：</p>
 * <ul>
 *   <li>加载最小化 Spring Boot 上下文</li>
 *   <li>不依赖外部服务（无数据库、Redis、Nacos）</li>
 *   <li>使用 @ActiveProfiles("test") 激活测试配置</li>
 * </ul>
 *
 * @author Test Team
 */
@Tag("integration")
@SpringBootTest(classes = TestApplication.class)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    // 可在此添加通用的测试工具方法
}
