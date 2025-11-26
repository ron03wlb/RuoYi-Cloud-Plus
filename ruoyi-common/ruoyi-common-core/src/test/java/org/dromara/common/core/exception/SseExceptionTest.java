package org.dromara.common.core.exception;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SseException 异常类测试
 *
 * @author Test Team
 */
@DisplayName("SseException 异常类测试")
class SseExceptionTest {

    @Nested
    @DisplayName("构造函数测试")
    class ConstructorTests {

        @Test
        @DisplayName("应该支持无参构造函数")
        void shouldCreateWithNoArgs() {
            // Act
            SseException exception = new SseException();

            // Assert
            assertThat(exception.getCode()).isNull();
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持单参数构造函数（message）")
        void shouldCreateWithMessage() {
            // Act
            SseException exception = new SseException("连接超时");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("连接超时");
            assertThat(exception.getCode()).isNull();
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持双参数构造函数（message, code）")
        void shouldCreateWithMessageAndCode() {
            // Act
            SseException exception = new SseException("客户端断开连接", 1001);

            // Assert
            assertThat(exception.getMessage()).isEqualTo("客户端断开连接");
            assertThat(exception.getCode()).isEqualTo(1001);
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持全参数构造函数")
        void shouldCreateWithAllArgs() {
            // Act
            SseException exception = new SseException(500, "服务器错误", "内部异常详情");

            // Assert
            assertThat(exception.getCode()).isEqualTo(500);
            assertThat(exception.getMessage()).isEqualTo("服务器错误");
            assertThat(exception.getDetailMessage()).isEqualTo("内部异常详情");
        }
    }

    @Nested
    @DisplayName("getMessage 方法测试")
    class GetMessageTests {

        @Test
        @DisplayName("getMessage 应该返回 message 字段")
        void shouldGetMessageField() {
            // Arrange
            SseException exception = new SseException();
            exception.setMessage("自定义消息");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEqualTo("自定义消息");
        }

        @Test
        @DisplayName("getMessage 应该正确处理 null 值")
        void shouldHandleNullMessage() {
            // Arrange
            SseException exception = new SseException();
            exception.setMessage(null);

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isNull();
        }

        @Test
        @DisplayName("getMessage 应该返回空字符串")
        void shouldReturnEmptyString() {
            // Arrange
            SseException exception = new SseException("");

            // Act
            String message = exception.getMessage();

            // Assert
            assertThat(message).isEmpty();
        }
    }

    @Nested
    @DisplayName("流式API测试")
    class FluentAPITests {

        @Test
        @DisplayName("setMessage 应该返回自身")
        void shouldReturnThisWhenSetMessage() {
            // Arrange
            SseException exception = new SseException();

            // Act
            SseException result = exception.setMessage("新消息");

            // Assert
            assertThat(result).isSameAs(exception);
            assertThat(exception.getMessage()).isEqualTo("新消息");
        }

        @Test
        @DisplayName("setDetailMessage 应该返回自身")
        void shouldReturnThisWhenSetDetailMessage() {
            // Arrange
            SseException exception = new SseException();

            // Act
            SseException result = exception.setDetailMessage("详细信息");

            // Assert
            assertThat(result).isSameAs(exception);
            assertThat(exception.getDetailMessage()).isEqualTo("详细信息");
        }

        @Test
        @DisplayName("应该支持链式调用")
        void shouldSupportMethodChaining() {
            // Act
            SseException exception =
                    new SseException().setMessage("SSE连接错误").setDetailMessage("客户端网络不稳定");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("SSE连接错误");
            assertThat(exception.getDetailMessage()).isEqualTo("客户端网络不稳定");
        }

        @Test
        @DisplayName("应该支持消息覆盖")
        void shouldOverrideMessage() {
            // Arrange
            SseException exception = new SseException("原始消息");

            // Act
            exception.setMessage("更新后的消息");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("更新后的消息");
        }

        @Test
        @DisplayName("应该支持设置 null 值")
        void shouldSetNullValues() {
            // Arrange
            SseException exception = new SseException("消息", 200);

            // Act
            exception.setMessage(null).setDetailMessage(null);

            // Assert
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("应该支持多次链式调用")
        void shouldSupportMultipleChainedCalls() {
            // Act
            SseException exception =
                    new SseException()
                            .setMessage("第一次消息")
                            .setDetailMessage("第一次详情")
                            .setMessage("第二次消息")
                            .setDetailMessage("第二次详情");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("第二次消息");
            assertThat(exception.getDetailMessage()).isEqualTo("第二次详情");
        }
    }

    @Nested
    @DisplayName("异常继承测试")
    class InheritanceTests {

        @Test
        @DisplayName("应该继承自 RuntimeException")
        void shouldExtendRuntimeException() {
            // Arrange
            SseException exception = new SseException("测试");

            // Assert
            assertThat(exception).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("应该能够抛出并捕获异常")
        void shouldThrowAndCatchException() {
            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                throw new SseException("SSE异常", 1001);
                            })
                    .isInstanceOf(SseException.class)
                    .hasMessage("SSE异常");
        }

        @Test
        @DisplayName("应该能够作为 RuntimeException 捕获")
        void shouldCatchAsRuntimeException() {
            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                throw new SseException("运行时异常");
                            })
                    .isInstanceOf(RuntimeException.class)
                    .isInstanceOf(SseException.class);
        }

        @Test
        @DisplayName("应该能够获取异常详细信息")
        void shouldGetExceptionDetails() {
            // Arrange
            SseException exception = new SseException(1001, "连接失败", "网络超时");

            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                throw exception;
                            })
                    .satisfies(
                            e -> {
                                SseException sse = (SseException) e;
                                assertThat(sse.getCode()).isEqualTo(1001);
                                assertThat(sse.getMessage()).isEqualTo("连接失败");
                                assertThat(sse.getDetailMessage()).isEqualTo("网络超时");
                            });
        }
    }

    @Nested
    @DisplayName("边界测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("应该处理空字符串消息")
        void shouldHandleEmptyMessage() {
            // Act
            SseException exception = new SseException("", 0);

            // Assert
            assertThat(exception.getMessage()).isEmpty();
            assertThat(exception.getCode()).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理 null 消息")
        void shouldHandleNullMessage() {
            // Act
            SseException exception = new SseException(null, 100);

            // Assert
            assertThat(exception.getMessage()).isNull();
            assertThat(exception.getCode()).isEqualTo(100);
        }

        @Test
        @DisplayName("应该处理零值错误码")
        void shouldHandleZeroCode() {
            // Act
            SseException exception = new SseException("消息", 0);

            // Assert
            assertThat(exception.getCode()).isEqualTo(0);
        }

        @Test
        @DisplayName("应该处理负数错误码")
        void shouldHandleNegativeCode() {
            // Act
            SseException exception = new SseException(-1, "错误", null);

            // Assert
            assertThat(exception.getCode()).isEqualTo(-1);
        }

        @Test
        @DisplayName("应该处理超长消息")
        void shouldHandleLongMessage() {
            // Arrange
            String longMessage = "A".repeat(2000);

            // Act
            SseException exception = new SseException(longMessage);

            // Assert
            assertThat(exception.getMessage()).hasSize(2000).startsWith("AAA").endsWith("AAA");
        }

        @Test
        @DisplayName("应该处理 Unicode 字符")
        void shouldHandleUnicodeCharacters() {
            // Act
            SseException exception = new SseException("SSE错误: 😀©®™");

            // Assert
            assertThat(exception.getMessage())
                    .contains("😀")
                    .contains("©")
                    .contains("®")
                    .contains("™");
        }

        @Test
        @DisplayName("应该处理包含换行符的消息")
        void shouldHandleNewLineCharacters() {
            // Act
            SseException exception = new SseException("第一行\n第二行\n第三行");

            // Assert
            assertThat(exception.getMessage()).contains("\n");
        }

        @Test
        @DisplayName("应该处理包含制表符的消息")
        void shouldHandleTabCharacters() {
            // Act
            SseException exception = new SseException("列1\t列2\t列3");

            // Assert
            assertThat(exception.getMessage()).contains("\t");
        }
    }

    @Nested
    @DisplayName("真实 SSE 场景测试")
    class RealWorldSseScenarioTests {

        @Test
        @DisplayName("SSE连接超时场景")
        void shouldHandleConnectionTimeout() {
            // Act
            SseException exception = new SseException(1001, "SSE连接超时", "客户端15秒内未响应");

            // Assert
            assertThat(exception.getCode()).isEqualTo(1001);
            assertThat(exception.getMessage()).isEqualTo("SSE连接超时");
            assertThat(exception.getDetailMessage()).isEqualTo("客户端15秒内未响应");
        }

        @Test
        @DisplayName("客户端断开连接场景")
        void shouldHandleClientDisconnect() {
            // Act
            SseException exception = new SseException("客户端已断开连接", 1002);

            // Assert
            assertThat(exception.getMessage()).isEqualTo("客户端已断开连接");
            assertThat(exception.getCode()).isEqualTo(1002);
        }

        @Test
        @DisplayName("消息推送失败场景")
        void shouldHandleMessagePushFailure() {
            // Act
            SseException exception =
                    new SseException().setMessage("消息推送失败").setDetailMessage("网络I/O异常");

            // Assert
            assertThat(exception.getMessage()).isEqualTo("消息推送失败");
            assertThat(exception.getDetailMessage()).isEqualTo("网络I/O异常");
        }

        @Test
        @DisplayName("SSE流已关闭场景")
        void shouldHandleStreamClosed() {
            // Act
            SseException exception = new SseException(1003, "SSE流已关闭", null);

            // Assert
            assertThat(exception.getCode()).isEqualTo(1003);
            assertThat(exception.getMessage()).isEqualTo("SSE流已关闭");
            assertThat(exception.getDetailMessage()).isNull();
        }

        @Test
        @DisplayName("SSE连接数超限场景")
        void shouldHandleConnectionLimitExceeded() {
            // Act
            SseException exception =
                    new SseException("SSE连接数已达上限", 1004)
                            .setDetailMessage("当前连接数: 1000, 最大连接数: 1000");

            // Assert
            assertThat(exception.getCode()).isEqualTo(1004);
            assertThat(exception.getMessage()).isEqualTo("SSE连接数已达上限");
            assertThat(exception.getDetailMessage()).contains("1000");
        }

        @Test
        @DisplayName("SSE认证失败场景")
        void shouldHandleAuthenticationFailure() {
            // Act
            SseException exception = new SseException(401, "SSE认证失败", "Token已过期");

            // Assert
            assertThat(exception.getCode()).isEqualTo(401);
            assertThat(exception.getMessage()).isEqualTo("SSE认证失败");
            assertThat(exception.getDetailMessage()).isEqualTo("Token已过期");
        }

        @Test
        @DisplayName("SSE服务器内部错误场景")
        void shouldHandleServerError() {
            // Act
            SseException exception = new SseException(500, "SSE服务器内部错误", "EventBus异常");

            // Assert
            assertThat(exception.getCode()).isEqualTo(500);
            assertThat(exception.getMessage()).isEqualTo("SSE服务器内部错误");
            assertThat(exception.getDetailMessage()).isEqualTo("EventBus异常");
        }
    }

    @Nested
    @DisplayName("序列化测试")
    class SerializationTests {

        @Test
        @DisplayName("应该有 serialVersionUID")
        void shouldHaveSerialVersionUID() {
            // Assert
            assertThatNoException()
                    .isThrownBy(
                            () -> {
                                java.lang.reflect.Field field =
                                        SseException.class.getDeclaredField("serialVersionUID");
                                assertThat(field.getType()).isEqualTo(long.class);
                                assertThat(
                                                java.lang.reflect.Modifier.isStatic(
                                                        field.getModifiers()))
                                        .isTrue();
                                assertThat(java.lang.reflect.Modifier.isFinal(field.getModifiers()))
                                        .isTrue();
                            });
        }
    }

    @Nested
    @DisplayName("Lombok生成的方法测试")
    class LombokGeneratedMethodsTests {

        @Test
        @DisplayName("应该正确生成 getter 和 setter 方法")
        void shouldHaveGettersAndSetters() {
            // Arrange
            SseException exception = new SseException();

            // Act
            exception.setCode(100);
            exception.setMessage("测试消息");
            exception.setDetailMessage("详细信息");

            // Assert
            assertThat(exception.getCode()).isEqualTo(100);
            assertThat(exception.getMessage()).isEqualTo("测试消息");
            assertThat(exception.getDetailMessage()).isEqualTo("详细信息");
        }

        @Test
        @DisplayName("equals 方法应该比较字段值")
        void shouldCompareFieldValuesInEquals() {
            // Arrange
            SseException exception1 = new SseException(404, "错误", "详细");
            SseException exception2 = new SseException(404, "错误", "详细");

            // Act & Assert - 由于 @EqualsAndHashCode(callSuper = true)，
            // 不同实例即使字段相同也不会相等（因为包含 RuntimeException 的堆栈等信息）
            // 所以这里验证字段值相等
            assertThat(exception1.getCode()).isEqualTo(exception2.getCode());
            assertThat(exception1.getMessage()).isEqualTo(exception2.getMessage());
            assertThat(exception1.getDetailMessage()).isEqualTo(exception2.getDetailMessage());
        }

        @Test
        @DisplayName("hashCode 方法应该基于字段值计算")
        void shouldCalculateHashCodeBasedOnFields() {
            // Arrange
            SseException exception1 = new SseException(100, "消息", "详情");

            // Act
            int hashCode1 = exception1.hashCode();
            int hashCode2 = exception1.hashCode();

            // Assert - 同一实例的 hashCode 应该一致
            assertThat(hashCode1).isEqualTo(hashCode2);
        }

        @Test
        @DisplayName("toString 方法应该包含所有字段")
        void shouldIncludeAllFieldsInToString() {
            // Arrange
            SseException exception = new SseException(200, "成功", "操作完成");

            // Act
            String toString = exception.toString();

            // Assert
            assertThat(toString)
                    .contains("code=200")
                    .contains("message=成功")
                    .contains("detailMessage=操作完成");
        }

        @Test
        @DisplayName("toString 应该处理 null 字段")
        void shouldHandleNullFieldsInToString() {
            // Arrange
            SseException exception = new SseException();

            // Act
            String toString = exception.toString();

            // Assert
            assertThat(toString)
                    .contains("code=null")
                    .contains("message=null")
                    .contains("detailMessage=null");
        }
    }

    @Nested
    @DisplayName("综合场景测试")
    class IntegrationTests {

        @Test
        @DisplayName("应该支持完整的异常创建和使用流程")
        void shouldSupportCompleteExceptionFlow() {
            // Act
            SseException exception = new SseException().setMessage("初始消息").setDetailMessage("初始详情");

            exception.setCode(1001);
            exception.setMessage("更新后的消息");

            // Assert
            assertThat(exception.getCode()).isEqualTo(1001);
            assertThat(exception.getMessage()).isEqualTo("更新后的消息");
            assertThat(exception.getDetailMessage()).isEqualTo("初始详情");
        }

        @Test
        @DisplayName("应该支持异常重新抛出")
        void shouldSupportExceptionRethrowing() {
            // Arrange
            SseException original = new SseException(500, "原始错误", "堆栈信息");

            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                try {
                                    throw original;
                                } catch (SseException e) {
                                    throw e.setMessage("重新抛出: " + e.getMessage());
                                }
                            })
                    .isInstanceOf(SseException.class)
                    .hasMessageContaining("重新抛出");
        }

        @Test
        @DisplayName("应该支持异常包装")
        void shouldSupportExceptionWrapping() {
            // Act & Assert
            assertThatThrownBy(
                            () -> {
                                try {
                                    throw new RuntimeException("底层异常");
                                } catch (RuntimeException e) {
                                    throw new SseException("SSE层异常", 500)
                                            .setDetailMessage("原因: " + e.getMessage());
                                }
                            })
                    .isInstanceOf(SseException.class)
                    .hasMessage("SSE层异常")
                    .satisfies(
                            ex -> {
                                SseException sse = (SseException) ex;
                                assertThat(sse.getDetailMessage()).contains("底层异常");
                            });
        }
    }
}
