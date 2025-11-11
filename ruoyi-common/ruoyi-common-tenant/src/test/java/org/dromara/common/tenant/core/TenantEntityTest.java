package org.dromara.common.tenant.core;

import org.dromara.common.tenant.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TenantEntity 测试
 * <p>
 * 测试租户实体类的tenantId属性
 * </p>
 * <p>
 * 注意：TenantEntity继承自BaseEntity，BaseEntity的属性已在mybatis模块中测试，这里只测试tenantId字段
 * </p>
 *
 * @author Test Team
 */
@DisplayName("TenantEntity 测试")
class TenantEntityTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. tenantId属性测试")
    class TenantIdPropertyTests {

        @Test
        @DisplayName("应该正确设置和获取tenantId")
        void shouldSetAndGetTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String tenantId = "TENANT-001";

            // Act
            entity.setTenantId(tenantId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(tenantId);
        }

        @Test
        @DisplayName("应该支持null的tenantId")
        void shouldSupportNullTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();

            // Act
            entity.setTenantId(null);

            // Assert
            assertThat(entity.getTenantId()).isNull();
        }

        @Test
        @DisplayName("应该支持空字符串的tenantId")
        void shouldSupportEmptyTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();

            // Act
            entity.setTenantId("");

            // Assert
            assertThat(entity.getTenantId()).isEmpty();
        }

        @Test
        @DisplayName("应该支持特殊字符的tenantId")
        void shouldSupportSpecialCharactersTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String tenantId = "租户-001-测试@#$%";

            // Act
            entity.setTenantId(tenantId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(tenantId);
        }
    }

    @Nested
    @DisplayName("2. Lombok功能测试")
    class LombokFunctionTests {

        @Test
        @DisplayName("应该正确实现equals方法")
        void shouldImplementEquals() {
            // Arrange
            TenantEntity entity1 = new TenantEntity();
            entity1.setTenantId("TENANT-001");

            TenantEntity entity2 = new TenantEntity();
            entity2.setTenantId("TENANT-001");

            TenantEntity entity3 = new TenantEntity();
            entity3.setTenantId("TENANT-002");

            // Assert
            assertThat(entity1).isEqualTo(entity2);
            assertThat(entity1).isNotEqualTo(entity3);
        }

        @Test
        @DisplayName("应该正确实现hashCode方法")
        void shouldImplementHashCode() {
            // Arrange
            TenantEntity entity1 = new TenantEntity();
            entity1.setTenantId("TENANT-001");

            TenantEntity entity2 = new TenantEntity();
            entity2.setTenantId("TENANT-001");

            // Assert
            assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
        }

        @Test
        @DisplayName("应该正确实现toString方法")
        void shouldImplementToString() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            entity.setTenantId("TENANT-001");

            // Act
            String toString = entity.toString();

            // Assert
            assertThat(toString).contains("TenantEntity");
            assertThat(toString).contains("TENANT-001");
        }
    }

    @Nested
    @DisplayName("3. 业务场景测试")
    class BusinessScenarioTests {

        @Test
        @DisplayName("应该支持租户ID变更")
        void shouldSupportTenantIdChange() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String oldTenantId = "TENANT-001";
            String newTenantId = "TENANT-002";

            // Act
            entity.setTenantId(oldTenantId);
            assertThat(entity.getTenantId()).isEqualTo(oldTenantId);

            entity.setTenantId(newTenantId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(newTenantId);
        }

        @Test
        @DisplayName("应该支持UUID格式的租户ID")
        void shouldSupportUUIDTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String uuidTenantId = "550e8400-e29b-41d4-a716-446655440000";

            // Act
            entity.setTenantId(uuidTenantId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(uuidTenantId);
        }

        @Test
        @DisplayName("应该支持数字字符串格式的租户ID")
        void shouldSupportNumericStringTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String numericTenantId = "123456789";

            // Act
            entity.setTenantId(numericTenantId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(numericTenantId);
        }

        @Test
        @DisplayName("应该支持分层租户ID")
        void shouldSupportHierarchicalTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String hierarchicalId = "ORG-001:DEPT-002:TEAM-003";

            // Act
            entity.setTenantId(hierarchicalId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(hierarchicalId);
        }
    }

    @Nested
    @DisplayName("4. 边界条件测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理超长租户ID")
        void shouldHandleLongTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String longTenantId = "T".repeat(1000);

            // Act
            entity.setTenantId(longTenantId);

            // Assert
            assertThat(entity.getTenantId()).hasSize(1000);
        }

        @Test
        @DisplayName("应该处理包含换行符的租户ID")
        void shouldHandleTenantIdWithNewline() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String tenantIdWithNewline = "TENANT\n001";

            // Act
            entity.setTenantId(tenantIdWithNewline);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(tenantIdWithNewline);
        }

        @Test
        @DisplayName("应该处理包含空格的租户ID")
        void shouldHandleTenantIdWithSpaces() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String tenantIdWithSpaces = "TENANT 001 TEST";

            // Act
            entity.setTenantId(tenantIdWithSpaces);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(tenantIdWithSpaces);
        }

        @Test
        @DisplayName("应该处理Unicode字符的租户ID")
        void shouldHandleUnicodeTenantId() {
            // Arrange
            TenantEntity entity = new TenantEntity();
            String unicodeTenantId = "租户🏢企业😀测试";

            // Act
            entity.setTenantId(unicodeTenantId);

            // Assert
            assertThat(entity.getTenantId()).isEqualTo(unicodeTenantId);
        }
    }
}
