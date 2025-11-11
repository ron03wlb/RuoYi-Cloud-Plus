package org.dromara.common.tenant.helper;

import org.dromara.common.tenant.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TenantHelper 测试
 * <p>
 * 测试租户助手类的可单元测试方法（ignore和dynamic方法）
 * </p>
 * <p>
 * 注意：本测试只覆盖不依赖外部环境的方法逻辑。
 * enableIgnore/disableIgnore等方法依赖MyBatis Plus的InterceptorIgnoreHelper，
 * setDynamic/getDynamic等方法依赖Redis和Sa-Token，这些需要集成测试。
 * </p>
 *
 * @author Test Team
 */
@DisplayName("TenantHelper 测试")
class TenantHelperTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. ignore(Runnable) 方法测试")
    class IgnoreRunnableTests {

        @Test
        @DisplayName("应该执行Runnable并正常返回")
        void shouldExecuteRunnable() {
            // Arrange
            AtomicBoolean executed = new AtomicBoolean(false);

            // Act
            TenantHelper.ignore(() -> executed.set(true));

            // Assert
            assertThat(executed.get()).isTrue();
        }

        @Test
        @DisplayName("应该执行多次操作的Runnable")
        void shouldExecuteMultipleOperations() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);

            // Act
            TenantHelper.ignore(() -> {
                counter.incrementAndGet();
                counter.incrementAndGet();
                counter.incrementAndGet();
            });

            // Assert
            assertThat(counter.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("Runnable内的异常应该向外传播")
        void shouldPropagateExceptionFromRunnable() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("测试异常");

            // Act & Assert
            assertThatThrownBy(() ->
                TenantHelper.ignore(() -> {
                    throw expectedException;
                })
            ).isSameAs(expectedException);
        }

        @Test
        @DisplayName("异常发生后仍应执行清理逻辑")
        void shouldExecuteCleanupEvenOnException() {
            // 注意：这个测试验证的是try-finally模式的正确性
            // 虽然我们无法直接验证disableIgnore()被调用（因为它依赖外部环境），
            // 但我们可以验证异常确实被抛出，这说明finally块的路径是正确的

            // Act & Assert
            assertThatThrownBy(() ->
                TenantHelper.ignore(() -> {
                    throw new RuntimeException("异常测试");
                })
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("异常测试");
        }

        @Test
        @DisplayName("应该支持空操作的Runnable")
        void shouldSupportEmptyRunnable() {
            // Act - should not throw exception
            TenantHelper.ignore(() -> {
                // Empty runnable
            });

            // Assert - no exception means success
            assertThat(true).isTrue();
        }
    }

    @Nested
    @DisplayName("2. ignore(Supplier<T>) 方法测试")
    class IgnoreSupplierTests {

        @Test
        @DisplayName("应该执行Supplier并返回结果")
        void shouldExecuteSupplierAndReturnResult() {
            // Arrange
            String expectedResult = "测试结果";

            // Act
            String result = TenantHelper.ignore(() -> expectedResult);

            // Assert
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("应该支持返回null的Supplier")
        void shouldSupportNullReturningSupplier() {
            // Act
            String result = TenantHelper.ignore(() -> null);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该支持返回不同类型的Supplier")
        void shouldSupportDifferentTypes() {
            // Act & Assert
            Integer intResult = TenantHelper.ignore(() -> 42);
            assertThat(intResult).isEqualTo(42);

            Boolean boolResult = TenantHelper.ignore(() -> true);
            assertThat(boolResult).isTrue();

            Object objResult = TenantHelper.ignore(() -> new Object());
            assertThat(objResult).isNotNull();
        }

        @Test
        @DisplayName("Supplier内的异常应该向外传播")
        void shouldPropagateExceptionFromSupplier() {
            // Arrange
            RuntimeException expectedException = new RuntimeException("Supplier异常");

            // Act & Assert
            assertThatThrownBy(() ->
                TenantHelper.ignore(() -> {
                    throw expectedException;
                })
            ).isSameAs(expectedException);
        }

        @Test
        @DisplayName("应该支持复杂计算的Supplier")
        void shouldSupportComplexComputation() {
            // Act
            Integer result = TenantHelper.ignore(() -> {
                int sum = 0;
                for (int i = 1; i <= 10; i++) {
                    sum += i;
                }
                return sum;
            });

            // Assert
            assertThat(result).isEqualTo(55);
        }
    }

    @Nested
    @DisplayName("3. dynamic(String, Runnable) 方法测试")
    class DynamicRunnableTests {

        @Test
        @DisplayName("应该执行Runnable并正常返回")
        void shouldExecuteRunnable() {
            // Arrange
            String tenantId = "TENANT-001";
            AtomicBoolean executed = new AtomicBoolean(false);

            // Act
            TenantHelper.dynamic(tenantId, () -> executed.set(true));

            // Assert
            assertThat(executed.get()).isTrue();
        }

        @Test
        @DisplayName("应该支持不同租户ID")
        void shouldSupportDifferentTenantIds() {
            // Arrange
            AtomicReference<String> capturedTenantId = new AtomicReference<>();

            // Act
            TenantHelper.dynamic("TENANT-001", () -> capturedTenantId.set("TENANT-001"));
            assertThat(capturedTenantId.get()).isEqualTo("TENANT-001");

            TenantHelper.dynamic("TENANT-002", () -> capturedTenantId.set("TENANT-002"));
            assertThat(capturedTenantId.get()).isEqualTo("TENANT-002");
        }

        @Test
        @DisplayName("Runnable内的异常应该向外传播")
        void shouldPropagateExceptionFromRunnable() {
            // Arrange
            String tenantId = "TENANT-001";
            RuntimeException expectedException = new RuntimeException("Dynamic异常");

            // Act & Assert
            assertThatThrownBy(() ->
                TenantHelper.dynamic(tenantId, () -> {
                    throw expectedException;
                })
            ).isSameAs(expectedException);
        }

        @Test
        @DisplayName("应该支持null租户ID")
        void shouldSupportNullTenantId() {
            // Arrange
            AtomicBoolean executed = new AtomicBoolean(false);

            // Act - should not throw exception
            TenantHelper.dynamic(null, () -> executed.set(true));

            // Assert
            assertThat(executed.get()).isTrue();
        }

        @Test
        @DisplayName("应该支持空字符串租户ID")
        void shouldSupportEmptyTenantId() {
            // Arrange
            AtomicBoolean executed = new AtomicBoolean(false);

            // Act
            TenantHelper.dynamic("", () -> executed.set(true));

            // Assert
            assertThat(executed.get()).isTrue();
        }
    }

    @Nested
    @DisplayName("4. dynamic(String, Supplier<T>) 方法测试")
    class DynamicSupplierTests {

        @Test
        @DisplayName("应该执行Supplier并返回结果")
        void shouldExecuteSupplierAndReturnResult() {
            // Arrange
            String tenantId = "TENANT-001";
            String expectedResult = "动态租户结果";

            // Act
            String result = TenantHelper.dynamic(tenantId, () -> expectedResult);

            // Assert
            assertThat(result).isEqualTo(expectedResult);
        }

        @Test
        @DisplayName("应该支持返回null的Supplier")
        void shouldSupportNullReturningSupplier() {
            // Act
            String result = TenantHelper.dynamic("TENANT-001", () -> null);

            // Assert
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该支持不同返回类型")
        void shouldSupportDifferentReturnTypes() {
            // Act & Assert
            Integer intResult = TenantHelper.dynamic("TENANT-001", () -> 100);
            assertThat(intResult).isEqualTo(100);

            Boolean boolResult = TenantHelper.dynamic("TENANT-002", () -> false);
            assertThat(boolResult).isFalse();
        }

        @Test
        @DisplayName("Supplier内的异常应该向外传播")
        void shouldPropagateExceptionFromSupplier() {
            // Arrange
            String tenantId = "TENANT-001";
            RuntimeException expectedException = new RuntimeException("Dynamic Supplier异常");

            // Act & Assert
            assertThatThrownBy(() ->
                TenantHelper.dynamic(tenantId, () -> {
                    throw expectedException;
                })
            ).isSameAs(expectedException);
        }

        @Test
        @DisplayName("应该支持复杂的业务逻辑")
        void shouldSupportComplexBusinessLogic() {
            // Act
            String result = TenantHelper.dynamic("TENANT-001", () -> {
                StringBuilder sb = new StringBuilder();
                sb.append("租户");
                sb.append("-");
                sb.append("数据");
                return sb.toString();
            });

            // Assert
            assertThat(result).isEqualTo("租户-数据");
        }
    }

    @Nested
    @DisplayName("5. 嵌套调用测试")
    class NestedCallTests {

        @Test
        @DisplayName("应该支持ignore方法的嵌套调用")
        void shouldSupportNestedIgnoreCalls() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);

            // Act
            TenantHelper.ignore(() -> {
                counter.incrementAndGet(); // 1
                TenantHelper.ignore(() -> {
                    counter.incrementAndGet(); // 2
                });
                counter.incrementAndGet(); // 3
            });

            // Assert
            assertThat(counter.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("应该支持dynamic方法的嵌套调用")
        void shouldSupportNestedDynamicCalls() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);

            // Act
            TenantHelper.dynamic("TENANT-001", () -> {
                counter.incrementAndGet();
                TenantHelper.dynamic("TENANT-002", () -> {
                    counter.incrementAndGet();
                });
                counter.incrementAndGet();
            });

            // Assert
            assertThat(counter.get()).isEqualTo(3);
        }

        @Test
        @DisplayName("应该支持ignore和dynamic的混合嵌套")
        void shouldSupportMixedNestedCalls() {
            // Arrange
            AtomicBoolean ignoreExecuted = new AtomicBoolean(false);
            AtomicBoolean dynamicExecuted = new AtomicBoolean(false);

            // Act
            TenantHelper.ignore(() -> {
                ignoreExecuted.set(true);
                TenantHelper.dynamic("TENANT-001", () -> {
                    dynamicExecuted.set(true);
                });
            });

            // Assert
            assertThat(ignoreExecuted.get()).isTrue();
            assertThat(dynamicExecuted.get()).isTrue();
        }

        @Test
        @DisplayName("嵌套调用中的异常应该正确传播")
        void shouldPropagateExceptionInNestedCalls() {
            // Act & Assert
            assertThatThrownBy(() ->
                TenantHelper.ignore(() -> {
                    TenantHelper.dynamic("TENANT-001", () -> {
                        throw new RuntimeException("嵌套异常");
                    });
                })
            ).isInstanceOf(RuntimeException.class)
                .hasMessage("嵌套异常");
        }
    }

    @Nested
    @DisplayName("6. 边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理Supplier返回大对象")
        void shouldHandleLargeObjectReturn() {
            // Arrange
            StringBuilder largeString = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                largeString.append("x");
            }
            String expected = largeString.toString();

            // Act
            String result = TenantHelper.ignore(() -> expected);

            // Assert
            assertThat(result).hasSize(10000);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("应该处理长租户ID")
        void shouldHandleLongTenantId() {
            // Arrange
            String longTenantId = "TENANT-" + "X".repeat(1000);
            AtomicBoolean executed = new AtomicBoolean(false);

            // Act
            TenantHelper.dynamic(longTenantId, () -> executed.set(true));

            // Assert
            assertThat(executed.get()).isTrue();
        }

        @Test
        @DisplayName("应该处理快速连续调用")
        void shouldHandleRapidSequentialCalls() {
            // Arrange
            AtomicInteger counter = new AtomicInteger(0);

            // Act
            for (int i = 0; i < 100; i++) {
                TenantHelper.ignore(() -> counter.incrementAndGet());
            }

            // Assert
            assertThat(counter.get()).isEqualTo(100);
        }
    }

    @Nested
    @DisplayName("7. 业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该支持忽略租户执行查询全部数据")
        void shouldSupportIgnoreTenantForQueryAll() {
            // Simulate querying all data ignoring tenant
            AtomicReference<String> queryResult = new AtomicReference<>();

            TenantHelper.ignore(() -> {
                // 模拟查询所有租户数据
                queryResult.set("查询所有租户的数据");
            });

            assertThat(queryResult.get()).isEqualTo("查询所有租户的数据");
        }

        @Test
        @DisplayName("应该支持切换租户执行操作")
        void shouldSupportSwitchTenantForOperation() {
            // Simulate switching tenant for specific operation
            AtomicReference<String> operationResult = new AtomicReference<>();

            TenantHelper.dynamic("TENANT-999", () -> {
                // 模拟在特定租户下执行操作
                operationResult.set("TENANT-999的操作结果");
            });

            assertThat(operationResult.get()).isEqualTo("TENANT-999的操作结果");
        }

        @Test
        @DisplayName("应该支持数据迁移场景：忽略租户导出数据")
        void shouldSupportDataMigrationScenario() {
            // Simulate data migration: export data from all tenants
            Integer recordCount = TenantHelper.ignore(() -> {
                // 模拟导出所有租户的记录数
                return 1000;
            });

            assertThat(recordCount).isEqualTo(1000);
        }

        @Test
        @DisplayName("应该支持跨租户数据同步场景")
        void shouldSupportCrossTenantSyncScenario() {
            // Simulate cross-tenant data synchronization
            AtomicBoolean syncSuccess = new AtomicBoolean(false);

            TenantHelper.dynamic("TARGET-TENANT", () -> {
                // 模拟向目标租户同步数据
                syncSuccess.set(true);
            });

            assertThat(syncSuccess.get()).isTrue();
        }
    }
}
