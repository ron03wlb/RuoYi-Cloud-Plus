package org.dromara.auth;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 集成测试基类
 *
 * <p>提供完整的 Spring Boot 测试环境
 *
 * <p>特性:
 *
 * <ul>
 *   <li>加载完整 Spring Boot 上下文
 *   <li>提供 MockMvc 用于 Controller 层测试
 *   <li>使用 test profile 配置
 *   <li>随机端口启动（避免端口冲突）
 * </ul>
 *
 * <p>适用场景:
 *
 * <ul>
 *   <li>Controller 层集成测试
 *   <li>完整登录流程测试
 *   <li>需要 Spring 容器支持的测试
 * </ul>
 *
 * <p>使用示例:
 *
 * <pre>{@code
 * class TokenControllerIntegrationTest extends BaseIntegrationTest {
 *
 *     @Test
 *     void testLogin() throws Exception {
 *         mockMvc.perform(post("/login")
 *             .contentType(MediaType.APPLICATION_JSON)
 *             .content(loginBody))
 *             .andExpect(status().isOk());
 *     }
 * }
 * }</pre>
 *
 * @author Test Team
 */
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
      "spring.cloud.nacos.discovery.enabled=false",
      "spring.cloud.nacos.config.enabled=false",
      "dubbo.registry.address=N/A",
      "dubbo.protocol.port=-1"
    })
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

  @Autowired protected MockMvc mockMvc;

  /**
   * 测试前初始化
   *
   * <p>子类可以覆盖此方法进行自定义初始化
   */
  @BeforeEach
  public void baseSetUp() {
    // 子类可以覆盖此方法进行额外的初始化
  }
}
