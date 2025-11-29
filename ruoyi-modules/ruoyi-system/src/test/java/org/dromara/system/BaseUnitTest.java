package org.dromara.system;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 *
 * <p>提供 Mockito 支持的轻量级单元测试环境
 *
 * <p>特性:
 *
 * <ul>
 *   <li>Mockito 扩展支持
 *   <li>@Mock 注解支持
 *   <li>@InjectMocks 注解支持
 *   <li>不启动 Spring 容器，测试执行速度快
 * </ul>
 *
 * <p>适用场景:
 *
 * <ul>
 *   <li>Service 层单元测试
 *   <li>工具类单元测试
 *   <li>业务逻辑验证测试
 *   <li>不需要 Spring 容器的独立组件测试
 * </ul>
 *
 * <p>使用示例:
 *
 * <pre>
 * class MyServiceTest extends BaseUnitTest {
 *     &#64;Mock
 *     private MyMapper mapper;
 *
 *     &#64;InjectMocks
 *     private MyServiceImpl service;
 *
 *     &#64;Test
 *     void testMethod() {
 *         // 测试代码
 *     }
 * }
 * </pre>
 *
 * @author Test Team
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {
  // 基类不需要额外的字段或方法
  // 子类通过继承获得 Mockito 支持
}
