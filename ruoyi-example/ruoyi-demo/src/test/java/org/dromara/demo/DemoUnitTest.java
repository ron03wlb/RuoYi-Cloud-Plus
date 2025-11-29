package org.dromara.demo;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 单元测试案例
 *
 * @author Lion Li
 */
@Disabled(
    "原因：ruoyi-demo 是示例模块，没有完整的Spring Boot主程序和配置文件。@SpringBootTest 需要一个完整的Spring"
        + " Boot应用上下文（包含@SpringBootApplication主类和application.yml）。建议：将此类移动到实际的Spring"
        + " Boot微服务模块（如ruoyi-system）中进行测试")
@SpringBootTest // 此注解只能在 springboot 主包下使用 需包含 main 方法与 yml 配置文件
@DisplayName("单元测试案例")
public class DemoUnitTest {

  @Value("${spring.application.name}")
  private String appName;

  @DisplayName("测试 @SpringBootTest @Test @DisplayName 注解")
  @Test
  public void testTest() {
    System.out.println(appName);
  }

  @Disabled
  @DisplayName("测试 @Disabled 注解")
  @Test
  public void testDisabled() {
    System.out.println(appName);
  }

  @DisplayName("测试 @RepeatedTest 注解")
  @RepeatedTest(3)
  public void testRepeatedTest() {
    System.out.println(666);
  }

  @BeforeAll
  public static void testBeforeAll() {
    System.out.println("@BeforeAll ==================");
  }

  @BeforeEach
  public void testBeforeEach() {
    System.out.println("@BeforeEach ==================");
  }

  @AfterEach
  public void testAfterEach() {
    System.out.println("@AfterEach ==================");
  }

  @AfterAll
  public static void testAfterAll() {
    System.out.println("@AfterAll ==================");
  }
}
