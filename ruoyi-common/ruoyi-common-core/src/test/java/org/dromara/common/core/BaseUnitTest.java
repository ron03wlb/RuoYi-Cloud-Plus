package org.dromara.common.core;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 单元测试基类
 * <p>
 * 特点：
 * - 纯单元测试，所有依赖都使用 Mock
 * - 不加载 Spring 容器
 * - 执行速度快
 * - 适用于工具类、Service 层单元测试
 * </p>
 *
 * @author Test Team
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    /**
     * 测试基类可以在这里添加通用的测试工具方法
     */

}
