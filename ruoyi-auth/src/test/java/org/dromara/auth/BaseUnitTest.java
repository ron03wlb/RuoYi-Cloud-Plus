package org.dromara.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 * <p>
 * 提供基本的 Mockito 支持和通用测试设置
 * </p>
 *
 * <p>适用场景:</p>
 * <ul>
 *   <li>Service 层单元测试</li>
 *   <li>工具类单元测试</li>
 *   <li>不需要 Spring 容器的测试</li>
 * </ul>
 *
 * <p>使用示例:</p>
 * <pre>{@code
 * class MyServiceTest extends BaseUnitTest {
 *
 *     @Mock
 *     private RemoteUserService remoteUserService;
 *
 *     @InjectMocks
 *     private SysLoginService sysLoginService;
 *
 *     @Test
 *     void testSomeMethod() {
 *         when(remoteUserService.getUserInfo(any())).thenReturn(user);
 *         // 测试逻辑
 *     }
 * }
 * }</pre>
 *
 * @author Test Team
 */
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    private AutoCloseable closeable;

    /**
     * 测试前初始化
     */
    @BeforeEach
    public void baseSetUp() {
        // 初始化 Mockito 注解
        closeable = MockitoAnnotations.openMocks(this);
    }

    /**
     * 测试后清理
     */
    @AfterEach
    public void baseTearDown() throws Exception {
        // 关闭 Mockito 资源
        if (closeable != null) {
            closeable.close();
        }
    }
}
