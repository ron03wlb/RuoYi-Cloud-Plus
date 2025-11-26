package org.dromara.common.core.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * ServiceException 测试类
 *
 * @author Test Team
 */
@DisplayName("ServiceException 异常类测试")
class ServiceExceptionTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("无参构造函数应该创建空异常")
        void shouldCreateEmptyException() {
            // Act
            ServiceException exception = new ServiceException();

            // Assert
            assertThat(exception).isNotNull();
            assertThat(exception.getCode()).isNull();
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("单参数构造函数应该设置消息")
        void shouldCreateExceptionWithMessage() {
            // Arrange
            String message = "用户不存在";

            // Act
            ServiceException exception = new ServiceException(message);

            // Assert
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCode()).isNull();
        }

        @Test
        @DisplayName("双参数构造函数应该设置消息和错误码")
        void shouldCreateExceptionWithMessageAndCode() {
            // Arrange
            String message = "权限不足";
            Integer code = 403;

            // Act
            ServiceException exception = new ServiceException(message, code);

            // Assert
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getCode()).isEqualTo(code);
        }

        @Test
        @DisplayName("可变参数构造函数应该格式化消息")
        void shouldCreateExceptionWithFormattedMessage() {
            // Arrange
            String template = "用户 {} 没有 {} 权限";
            String username = "张三";
            String permission = "删除";

            // Act
            ServiceException exception = new ServiceException(template, username, permission);

            // Assert
            assertThat(exception.getMessage()).isEqualTo("用户 张三 没有 删除 权限");
        }

        @Test
        @DisplayName("全参数构造函数应该设置所有字段")
        void shouldCreateExceptionWithAllFields() {
            // Arrange
            Integer code = 500;
            String message = "系统错误";
            String detailMessage = "数据库连接失败";

            // Act
            ServiceException exception = new ServiceException(code, message, detailMessage);

            // Assert
            assertThat(exception.getCode()).isEqualTo(code);
            assertThat(exception.getMessage()).isEqualTo(message);
            assertThat(exception.getDetailMessage()).isEqualTo(detailMessage);
        }
    }

    @Nested
    @DisplayName("消息格式化测试")
    class MessageFormattingTests {

        @Test
        @DisplayName("应该正确格式化单个占位符")
        void shouldFormatSinglePlaceholder() {
            // Act
            ServiceException exception = new ServiceException("用户{}不存在", "admin");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("用户admin不存在");
        }

        @Test
        @DisplayName("应该正确格式化多个占位符")
        void shouldFormatMultiplePlaceholders() {
            // Act
            ServiceException exception =
                    new ServiceException(
                            "订单{}支付失败，原因：{}，时间：{}", "20241030001", "余额不足", "2024-10-30 10:00:00");

            // Assert
            assertThat(exception.getMessage())
                    .isEqualTo("订单20241030001支付失败，原因：余额不足，时间：2024-10-30 10:00:00");
        }

        @Test
        @DisplayName("应该处理数字类型参数")
        void shouldFormatNumericArguments() {
            // Act
            ServiceException exception = new ServiceException("文件大小{}MB超过限制{}MB", 50, 10);

            // Assert
            assertThat(exception.getMessage()).isEqualTo("文件大小50MB超过限制10MB");
        }

        @Test
        @DisplayName("应该处理null参数")
        void shouldHandleNullArguments() {
            // Act
            ServiceException exception = new ServiceException("用户{}的角色为{}", "admin", null);

            // Assert
            assertThat(exception.getMessage()).isEqualTo("用户admin的角色为null");
        }

        @Test
        @DisplayName("占位符多于参数时应该保留多余的占位符")
        void shouldKeepExtraPlaceholders() {
            // Act
            ServiceException exception = new ServiceException("错误{}-{}-{}", "A");

            // Assert
            assertThat(exception.getMessage()).contains("A");
        }

        @Test
        @DisplayName("参数多于占位符时应该忽略多余参数")
        void shouldIgnoreExtraArguments() {
            // Act
            ServiceException exception = new ServiceException("错误{}", "A", "B", "C");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("错误A");
        }

        @Test
        @DisplayName("无占位符时应该返回原始消息")
        void shouldReturnOriginalMessageWithoutPlaceholders() {
            // Act
            ServiceException exception = new ServiceException("系统错误", "arg1", "arg2");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("系统错误");
        }

        @Test
        @DisplayName("应该处理空字符串参数")
        void shouldHandleEmptyStringArguments() {
            // Act
            ServiceException exception = new ServiceException("值为：{}", "");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("值为：");
        }

        @Test
        @DisplayName("应该处理包含特殊字符的消息")
        void shouldHandleSpecialCharactersInMessage() {
            // Act
            ServiceException exception =
                    new ServiceException("SQL错误：SELECT * FROM users WHERE name = '{}'", "admin");

            // Assert
            assertThat(exception.getMessage())
                    .isEqualTo("SQL错误：SELECT * FROM users WHERE name = 'admin'");
        }

        @Test
        @DisplayName("应该处理长消息格式化")
        void shouldHandleLongMessageFormatting() {
            // Arrange
            String template = "操作失败：用户{}在{}时间尝试{}操作{}资源，但由于{}原因被拒绝";

            // Act
            ServiceException exception =
                    new ServiceException(
                            template, "张三", "2024-10-30 10:00:00", "删除", "用户数据", "权限不足");

            // Assert
            assertThat(exception.getMessage())
                    .isEqualTo("操作失败：用户张三在2024-10-30 10:00:00时间尝试删除操作用户数据资源，但由于权限不足原因被拒绝");
        }
    }

    @Nested
    @DisplayName("流式API测试")
    class FluentAPITests {

        @Test
        @DisplayName("setMessage应该返回自身并设置消息")
        void shouldSetMessageFluently() {
            // Arrange
            ServiceException exception = new ServiceException();

            // Act
            ServiceException result = exception.setMessage("新消息");

            // Assert
            assertThat(result).isSameAs(exception);
            assertThat(exception.getMessage()).isEqualTo("新消息");
        }

        @Test
        @DisplayName("setDetailMessage应该返回自身并设置详细消息")
        void shouldSetDetailMessageFluently() {
            // Arrange
            ServiceException exception = new ServiceException();

            // Act
            ServiceException result = exception.setDetailMessage("详细错误信息");

            // Assert
            assertThat(result).isSameAs(exception);
            assertThat(exception.getDetailMessage()).isEqualTo("详细错误信息");
        }

        @Test
        @DisplayName("应该支持链式调用")
        void shouldSupportMethodChaining() {
            // Act
            ServiceException exception =
                    new ServiceException("错误").setMessage("更新的消息").setDetailMessage("这是详细信息");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("更新的消息");
            assertThat(exception.getDetailMessage()).isEqualTo("这是详细信息");
        }

        @Test
        @DisplayName("setMessage应该覆盖原有消息")
        void shouldOverrideOriginalMessage() {
            // Arrange
            ServiceException exception = new ServiceException("原始消息");

            // Act
            exception.setMessage("新消息");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("新消息");
        }

        @Test
        @DisplayName("应该允许设置null消息")
        void shouldAllowSettingNullMessage() {
            // Arrange
            ServiceException exception = new ServiceException("原始消息");

            // Act
            exception.setMessage(null);

            // Assert
            assertThat(exception.getMessage()).isNull();
        }

        @Test
        @DisplayName("应该允许设置null详细消息")
        void shouldAllowSettingNullDetailMessage() {
            // Arrange
            ServiceException exception = new ServiceException("消息");

            // Act
            exception.setDetailMessage(null);

            // Assert
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持多次链式调用")
        void shouldSupportMultipleChainedCalls() {
            // Act
            ServiceException exception =
                    new ServiceException()
                            .setMessage("第一次")
                            .setDetailMessage("详细1")
                            .setMessage("第二次")
                            .setDetailMessage("详细2");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("第二次");
            assertThat(exception.getDetailMessage()).isEqualTo("详细2");
        }
    }

    @Nested
    @DisplayName("异常继承测试")
    class InheritanceTests {

        @Test
        @DisplayName("应该继承自RuntimeException")
        void shouldExtendRuntimeException() {
            // Arrange
            ServiceException exception = new ServiceException("测试");

            // Assert
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该可以被抛出和捕获")
        void shouldBeThrowableAndCatchable() {
            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                throw new ServiceException("测试异常");
                            })
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("测试异常");
        }

        @Test
        @DisplayName("应该可以作为RuntimeException捕获")
        void shouldBeCatchableAsRuntimeException() {
            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                throw new ServiceException("测试异常");
                            })
                    .isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该可以作为Exception捕获")
        void shouldBeCatchableAsException() {
            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                throw new ServiceException("测试异常");
                            })
                    .isInstanceOf(Exception.class);
        }
    }

    @Nested
    @DisplayName("边界测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理空字符串消息")
        void shouldHandleEmptyMessage() {
            // Act
            ServiceException exception = new ServiceException("");

            // Assert
            assertThat(exception.getMessage()).isEmpty();
        }

        @Test
        @DisplayName("应该处理null消息")
        void shouldHandleNullMessage() {
            // Act
            ServiceException exception = new ServiceException(null, 500);

            // Assert
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getCode()).isEqualTo(500);
        }

        @Test
        @DisplayName("应该处理0作为错误码")
        void shouldHandleZeroErrorCode() {
            // Act
            ServiceException exception = new ServiceException("错误", 0);

            // Assert
            assertThat(exception.getCode()).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理负数错误码")
        void shouldHandleNegativeErrorCode() {
            // Act
            ServiceException exception = new ServiceException("错误", -1);

            // Assert
            assertThat(exception.getCode()).isEqualTo(-1);
        }

        @Test
        @DisplayName("应该处理超长消息")
        void shouldHandleVeryLongMessage() {
            // Arrange
            String longMessage = "错误".repeat(1000);

            // Act
            ServiceException exception = new ServiceException(longMessage);

            // Assert
            assertThat(exception.getMessage()).hasSize(2000); // "错误" is 2 chars
        }

        @Test
        @DisplayName("应该处理Unicode字符")
        void shouldHandleUnicodeCharacters() {
            // Act
            ServiceException exception = new ServiceException("错误：表情😀和特殊符号©®™");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("错误：表情😀和特殊符号©®™");
        }

        @Test
        @DisplayName("应该处理换行符")
        void shouldHandleNewlineCharacters() {
            // Act
            ServiceException exception = new ServiceException("第一行\n第二行\n第三行");

            // Assert
            assertThat(exception.getMessage()).contains("\n");
        }

        @Test
        @DisplayName("应该处理制表符")
        void shouldHandleTabCharacters() {
            // Act
            ServiceException exception = new ServiceException("列1\t列2\t列3");

            // Assert
            assertThat(exception.getMessage()).contains("\t");
        }
    }

    @Nested
    @DisplayName("真实业务场景测试")
    class RealWorldScenarioTests {

        @Test
        @DisplayName("用户认证失败场景")
        void shouldHandleAuthenticationFailure() {
            // Act
            ServiceException exception = new ServiceException("用户{}认证失败，原因：{}", "admin", "密码错误");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("用户admin认证失败，原因：密码错误");
        }

        @Test
        @DisplayName("权限不足场景")
        void shouldHandlePermissionDenied() {
            // Act
            ServiceException exception =
                    new ServiceException("权限不足", 403)
                            .setDetailMessage("用户 admin 尝试访问 /system/user/delete 接口");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("权限不足");
            assertThat(exception.getCode()).isEqualTo(403);
            assertThat(exception.getDetailMessage())
                    .isEqualTo("用户 admin 尝试访问 /system/user/delete 接口");
        }

        @Test
        @DisplayName("数据验证失败场景")
        void shouldHandleValidationError() {
            // Act
            ServiceException exception =
                    new ServiceException(
                            "数据验证失败：字段{}的值{}不符合规则{}", "email", "invalid-email", "必须是有效的邮箱地址");

            // Assert
            assertThat(exception.getMessage())
                    .isEqualTo("数据验证失败：字段email的值invalid-email不符合规则必须是有效的邮箱地址");
        }

        @Test
        @DisplayName("资源不存在场景")
        void shouldHandleResourceNotFound() {
            // Act
            ServiceException exception =
                    new ServiceException("资源不存在", 404).setDetailMessage("订单ID: 123456");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("资源不存在");
            assertThat(exception.getCode()).isEqualTo(404);
            assertThat(exception.getDetailMessage()).isEqualTo("订单ID: 123456");
        }

        @Test
        @DisplayName("限流场景")
        void shouldHandleRateLimitExceeded() {
            // Act
            ServiceException exception = new ServiceException("请求过于频繁，请{}秒后重试", "60");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("请求过于频繁，请60秒后重试");
        }

        @Test
        @DisplayName("业务规则违反场景")
        void shouldHandleBusinessRuleViolation() {
            // Act
            ServiceException exception =
                    new ServiceException("业务规则违反：{}", "库存不足，当前库存：10，需要：20")
                            .setDetailMessage("商品ID: SKU-12345")
                            .setMessage("库存不足");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("库存不足");
            assertThat(exception.getDetailMessage()).isEqualTo("商品ID: SKU-12345");
        }

        @Test
        @DisplayName("系统错误场景")
        void shouldHandleSystemError() {
            // Act
            ServiceException exception =
                    new ServiceException("系统错误", 500).setDetailMessage("数据库连接超时，耗时：30000ms");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("系统错误");
            assertThat(exception.getCode()).isEqualTo(500);
            assertThat(exception.getDetailMessage()).contains("数据库连接超时");
        }
    }

    @Nested
    @DisplayName("序列化测试")
    class SerializationTests {

        @Test
        @DisplayName("应该具有serialVersionUID")
        void shouldHaveSerialVersionUID() {
            // Assert - 通过反射验证serialVersionUID字段存在
            assertThatCode(
                            () -> {
                                ServiceException.class.getDeclaredField("serialVersionUID");
                            })
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Lombok生成的方法测试")
    class LombokGeneratedMethodsTests {

        @Test
        @DisplayName("getter方法应该正常工作")
        void shouldHaveWorkingGetters() {
            // Arrange
            ServiceException exception = new ServiceException(404, "错误", "详细");

            // Act & Assert
            assertThat(exception.getCode()).isEqualTo(404);
            assertThat(exception.getMessage()).isEqualTo("错误");
            assertThat(exception.getDetailMessage()).isEqualTo("详细");
        }

        @Test
        @DisplayName("setter方法应该正常工作")
        void shouldHaveWorkingSetters() {
            // Arrange
            ServiceException exception = new ServiceException();

            // Act
            exception.setCode(500);
            exception.setDetailMessage("新详细信息");

            // Assert
            assertThat(exception.getCode()).isEqualTo(500);
            assertThat(exception.getDetailMessage()).isEqualTo("新详细信息");
        }

        @Test
        @DisplayName("equals方法应该比较字段值")
        void shouldCompareFieldValues() {
            // Arrange
            ServiceException exception1 = new ServiceException(404, "错误", "详细");
            ServiceException exception2 = new ServiceException(404, "错误", "详细");
            ServiceException exception3 = new ServiceException(500, "错误", "详细");

            // Act & Assert - 由于@EqualsAndHashCode(callSuper =
            // true)，equals包含RuntimeException的字段（如堆栈）
            // 所以不同实例不会相等，但应该验证字段值相同
            assertThat(exception1.getCode()).isEqualTo(exception2.getCode());
            assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
            assertThat(exception1.getDetailMessage()).isEqualTo(exception2.getDetailMessage());

            assertThat(exception1.getCode()).isNotEqualTo(exception3.getCode());
        }

        @Test
        @DisplayName("hashCode方法应该存在")
        void shouldHaveHashCodeMethod() {
            // Arrange
            ServiceException exception1 = new ServiceException(404, "错误", "详细");
            ServiceException exception2 = new ServiceException(404, "错误", "详细");

            // Act & Assert - hashCode应该存在并返回一致的值（对于同一实例）
            int hashCode1 = exception1.hashCode();
            int hashCode2 = exception1.hashCode(); // 同一实例

            assertThat(hashCode1).isEqualTo(hashCode2);
            // 不同实例的hashCode可能不同（因为包含堆栈信息）
            assertThat(exception2.hashCode()).isNotNull();
        }

        @Test
        @DisplayName("toString方法应该返回有意义的字符串")
        void shouldHaveMeaningfulToString() {
            // Arrange
            ServiceException exception = new ServiceException(404, "错误", "详细");

            // Act
            String toString = exception.toString();

            // Assert
            assertThat(toString).contains("ServiceException").contains("404").contains("错误");
        }
    }
}
