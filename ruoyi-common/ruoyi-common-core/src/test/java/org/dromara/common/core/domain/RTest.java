package org.dromara.common.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.core.BaseUnitTest;
import org.dromara.common.core.constant.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * R（统一响应对象）单元测试.
 *
 * @author Test Team
 */
@DisplayName("R (统一响应对象) 单元测试")
class RTest extends BaseUnitTest {

  @Nested
  @DisplayName("1. 常量定义测试")
  class ConstantsTests {

    @Test
    @DisplayName("SUCCESS 常量应该等于 200")
    void shouldDefineSuccessConstant() {
      assertThat(R.SUCCESS).isEqualTo(200);
    }

    @Test
    @DisplayName("FAIL 常量应该等于 500")
    void shouldDefineFailConstant() {
      assertThat(R.FAIL).isEqualTo(500);
    }
  }

  @Nested
  @DisplayName("2. ok() 成功响应测试")
  class OkMethodsTests {

    @Test
    @DisplayName("ok() - 应该返回成功响应（无数据）")
    void shouldReturnSuccessWithoutData() {
      // Act
      R<Void> response = R.ok();

      // Assert
      assertThat(response.getCode()).isEqualTo(200);
      assertThat(response.getMsg()).isEqualTo("操作成功");
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("ok(data) - 应该返回成功响应（带数据）")
    void shouldReturnSuccessWithData() {
      // Arrange
      // 使用 Integer 而不是 String 避免方法重载歧义
      Integer data = 123;

      // Act
      R<Integer> response = R.ok(data);

      // Assert
      assertThat(response.getCode()).isEqualTo(200);
      assertThat(response.getMsg()).isEqualTo("操作成功");
      assertThat(response.getData()).isEqualTo(123);
    }

    @Test
    @DisplayName("ok(msg) - 应该返回成功响应（自定义消息）")
    void shouldReturnSuccessWithCustomMessage() {
      // Act
      R<Void> response = R.ok("自定义成功消息");

      // Assert
      assertThat(response.getCode()).isEqualTo(200);
      assertThat(response.getMsg()).isEqualTo("自定义成功消息");
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("ok(msg, data) - 应该返回成功响应（自定义消息 + 数据）")
    void shouldReturnSuccessWithCustomMessageAndData() {
      // Arrange
      Integer data = 123;

      // Act
      R<Integer> response = R.ok("创建成功", data);

      // Assert
      assertThat(response.getCode()).isEqualTo(200);
      assertThat(response.getMsg()).isEqualTo("创建成功");
      assertThat(response.getData()).isEqualTo(123);
    }

    @Test
    @DisplayName("ok() - 应该支持泛型类型")
    void shouldSupportGenericTypes() {
      // Arrange
      TestUser user = new TestUser("张三", 25);

      // Act
      R<TestUser> response = R.ok(user);

      // Assert
      assertThat(response.getData()).isNotNull();
      assertThat(response.getData().getName()).isEqualTo("张三");
      assertThat(response.getData().getAge()).isEqualTo(25);
    }
  }

  @Nested
  @DisplayName("3. fail() 失败响应测试")
  class FailMethodsTests {

    @Test
    @DisplayName("fail() - 应该返回失败响应（无数据）")
    void shouldReturnFailureWithoutData() {
      // Act
      R<Void> response = R.fail();

      // Assert
      assertThat(response.getCode()).isEqualTo(500);
      assertThat(response.getMsg()).isEqualTo("操作失败");
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("fail(msg) - 应该返回失败响应（自定义消息）")
    void shouldReturnFailureWithCustomMessage() {
      // Act
      R<Void> response = R.fail("用户名已存在");

      // Assert
      assertThat(response.getCode()).isEqualTo(500);
      assertThat(response.getMsg()).isEqualTo("用户名已存在");
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("fail(data) - 应该返回失败响应（带数据）")
    void shouldReturnFailureWithData() {
      // Arrange
      // 使用对象而不是 String 避免方法重载歧义
      // String 会匹配到 fail(String msg) 而不是 fail(T data)
      TestUser errorDetail = new TestUser("errorUser", 0);

      // Act
      R<TestUser> response = R.fail(errorDetail);

      // Assert
      assertThat(response.getCode()).isEqualTo(500);
      assertThat(response.getMsg()).isEqualTo("操作失败");
      assertThat(response.getData()).isNotNull();
      assertThat(response.getData().getName()).isEqualTo("errorUser");
    }

    @Test
    @DisplayName("fail(msg, data) - 应该返回失败响应（自定义消息 + 数据）")
    void shouldReturnFailureWithCustomMessageAndData() {
      // Arrange
      String errorData = "error stack trace";

      // Act
      R<String> response = R.fail("系统异常", errorData);

      // Assert
      assertThat(response.getCode()).isEqualTo(500);
      assertThat(response.getMsg()).isEqualTo("系统异常");
      assertThat(response.getData()).isEqualTo("error stack trace");
    }

    @Test
    @DisplayName("fail(code, msg) - 应该返回失败响应（自定义状态码 + 消息）")
    void shouldReturnFailureWithCustomCodeAndMessage() {
      // Act
      R<Void> response = R.fail(403, "权限不足");

      // Assert
      assertThat(response.getCode()).isEqualTo(403);
      assertThat(response.getMsg()).isEqualTo("权限不足");
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("fail(code, msg) - 应该支持各种HTTP状态码")
    void shouldSupportVariousHttpStatusCodes() {
      // 400 Bad Request
      R<Void> badRequest = R.fail(400, "请求参数错误");
      assertThat(badRequest.getCode()).isEqualTo(400);

      // 401 Unauthorized
      R<Void> unauthorized = R.fail(401, "未登录");
      assertThat(unauthorized.getCode()).isEqualTo(401);

      // 404 Not Found
      R<Void> notFound = R.fail(404, "资源不存在");
      assertThat(notFound.getCode()).isEqualTo(404);
    }
  }

  @Nested
  @DisplayName("4. warn() 警告响应测试")
  class WarnMethodsTests {

    @Test
    @DisplayName("warn(msg) - 应该返回警告响应（无数据）")
    void shouldReturnWarningWithoutData() {
      // Act
      R<Void> response = R.warn("数据可能不准确");

      // Assert
      assertThat(response.getCode()).isEqualTo(HttpStatus.WARN);
      assertThat(response.getMsg()).isEqualTo("数据可能不准确");
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("warn(msg, data) - 应该返回警告响应（带数据）")
    void shouldReturnWarningWithData() {
      // Arrange
      String warningData = "部分数据已过期";

      // Act
      R<String> response = R.warn("警告：数据同步延迟", warningData);

      // Assert
      assertThat(response.getCode()).isEqualTo(HttpStatus.WARN);
      assertThat(response.getMsg()).isEqualTo("警告：数据同步延迟");
      assertThat(response.getData()).isEqualTo("部分数据已过期");
    }

    @Test
    @DisplayName("HttpStatus.WARN - 验证警告状态码值")
    void shouldVerifyWarnStatusCode() {
      // 警告状态码应该是 601
      assertThat(HttpStatus.WARN).isEqualTo(601);
    }
  }

  @Nested
  @DisplayName("5. isSuccess() 和 isError() 判断方法测试")
  class StatusCheckMethodsTests {

    @Test
    @DisplayName("isSuccess() - 应该对成功响应返回 true")
    void shouldReturnTrueForSuccessResponse() {
      // Arrange
      R<String> response = R.ok("test");

      // Act & Assert
      assertThat(R.isSuccess(response)).isTrue();
      assertThat(R.isError(response)).isFalse();
    }

    @Test
    @DisplayName("isSuccess() - 应该对失败响应返回 false")
    void shouldReturnFalseForFailureResponse() {
      // Arrange
      R<Void> response = R.fail();

      // Act & Assert
      assertThat(R.isSuccess(response)).isFalse();
      assertThat(R.isError(response)).isTrue();
    }

    @Test
    @DisplayName("isSuccess() - 应该对自定义失败状态码返回 false")
    void shouldReturnFalseForCustomFailureCode() {
      // Arrange
      R<Void> response = R.fail(404, "Not Found");

      // Act & Assert
      assertThat(R.isSuccess(response)).isFalse();
      assertThat(R.isError(response)).isTrue();
    }

    @Test
    @DisplayName("isSuccess() - 应该对警告响应返回 false")
    void shouldReturnFalseForWarningResponse() {
      // Arrange
      R<Void> response = R.warn("警告信息");

      // Act & Assert
      assertThat(R.isSuccess(response)).isFalse();
      assertThat(R.isError(response)).isTrue();
    }

    @Test
    @DisplayName("isError() - 应该与 isSuccess() 结果相反")
    void shouldReturnOppositeOfIsSuccess() {
      // Arrange
      R<Void> success = R.ok();
      R<Void> failure = R.fail();

      // Assert
      assertThat(R.isSuccess(success)).isTrue();
      assertThat(R.isError(success)).isFalse();

      assertThat(R.isSuccess(failure)).isFalse();
      assertThat(R.isError(failure)).isTrue();
    }
  }

  @Nested
  @DisplayName("6. Lombok 生成方法测试")
  class LombokGeneratedMethodsTests {

    @Test
    @DisplayName("setter/getter - 应该正确设置和获取字段值")
    void shouldSetAndGetFields() {
      // Arrange
      R<String> response = new R<>();

      // Act
      response.setCode(200);
      response.setMsg("测试消息");
      response.setData("测试数据");

      // Assert
      assertThat(response.getCode()).isEqualTo(200);
      assertThat(response.getMsg()).isEqualTo("测试消息");
      assertThat(response.getData()).isEqualTo("测试数据");
    }

    @Test
    @DisplayName("无参构造函数 - 应该创建空对象")
    void shouldCreateEmptyObject() {
      // Act
      R<Void> response = new R<>();

      // Assert
      assertThat(response.getCode()).isZero();
      assertThat(response.getMsg()).isNull();
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("toString() - 应该包含所有字段信息")
    void shouldIncludeAllFieldsInToString() {
      // Arrange
      R<String> response = R.ok("数据", "测试");

      // Act
      String toString = response.toString();

      // Assert
      assertThat(toString).contains("code=200");
      assertThat(toString).contains("msg=数据");
      assertThat(toString).contains("data=测试");
    }
  }

  @Nested
  @DisplayName("7. 序列化测试")
  class SerializationTests {

    @Test
    @DisplayName("应该实现 Serializable 接口")
    void shouldImplementSerializable() {
      // Arrange
      R<String> response = R.ok("test");

      // Assert
      assertThat(response).isInstanceOf(java.io.Serializable.class);
    }

    @Test
    @DisplayName("serialVersionUID - 应该正确定义")
    void shouldHaveSerialVersionUID() throws NoSuchFieldException {
      // Arrange
      var field = R.class.getDeclaredField("serialVersionUID");

      // Assert
      assertThat(field.getType()).isEqualTo(long.class);
      assertThat(java.lang.reflect.Modifier.isStatic(field.getModifiers())).isTrue();
      assertThat(java.lang.reflect.Modifier.isFinal(field.getModifiers())).isTrue();
    }
  }

  @Nested
  @DisplayName("8. 边界测试")
  class BoundaryTests {

    @Test
    @DisplayName("应该正确处理 null 消息")
    void shouldHandleNullMessage() {
      // Act
      R<Void> response = R.ok(null);

      // Assert
      assertThat(response.getCode()).isEqualTo(200);
      assertThat(response.getMsg()).isNull();
      assertThat(response.getData()).isNull();
    }

    @Test
    @DisplayName("应该正确处理空字符串消息")
    void shouldHandleEmptyMessage() {
      // Act
      R<Void> response = R.fail("");

      // Assert
      assertThat(response.getCode()).isEqualTo(500);
      assertThat(response.getMsg()).isEmpty();
    }

    @Test
    @DisplayName("应该正确处理超长消息")
    void shouldHandleLongMessage() {
      // Arrange
      String longMsg = "x".repeat(1000);

      // Act
      R<Void> response = R.ok(longMsg);

      // Assert
      assertThat(response.getMsg()).hasSize(1000);
    }

    @Test
    @DisplayName("应该正确处理特殊字符消息")
    void shouldHandleSpecialCharacters() {
      // Act
      R<Void> response = R.fail("错误：<script>alert('xss')</script>");

      // Assert
      assertThat(response.getMsg()).contains("<script>");
    }

    @Test
    @DisplayName("应该正确处理负数状态码")
    void shouldHandleNegativeStatusCode() {
      // Act
      R<Void> response = R.fail(-1, "负数状态码");

      // Assert
      assertThat(response.getCode()).isEqualTo(-1);
    }
  }

  @Nested
  @DisplayName("9. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("用户登录成功场景")
    void shouldHandleLoginSuccess() {
      // Arrange
      TestUser user = new TestUser("admin", 30);

      // Act
      R<TestUser> response = R.ok("登录成功", user);

      // Assert
      assertThat(R.isSuccess(response)).isTrue();
      assertThat(response.getMsg()).isEqualTo("登录成功");
      assertThat(response.getData().getName()).isEqualTo("admin");
    }

    @Test
    @DisplayName("用户登录失败场景")
    void shouldHandleLoginFailure() {
      // Act
      R<Void> response = R.fail(401, "用户名或密码错误");

      // Assert
      assertThat(R.isError(response)).isTrue();
      assertThat(response.getCode()).isEqualTo(401);
      assertThat(response.getMsg()).isEqualTo("用户名或密码错误");
    }

    @Test
    @DisplayName("数据列表查询场景")
    void shouldHandleListQuery() {
      // Arrange
      java.util.List<String> dataList = java.util.Arrays.asList("item1", "item2", "item3");

      // Act
      R<java.util.List<String>> response = R.ok(dataList);

      // Assert
      assertThat(R.isSuccess(response)).isTrue();
      assertThat(response.getData()).hasSize(3);
    }

    @Test
    @DisplayName("资源不存在场景")
    void shouldHandleResourceNotFound() {
      // Act
      R<Void> response = R.fail(404, "用户不存在");

      // Assert
      assertThat(response.getCode()).isEqualTo(404);
      assertThat(response.getMsg()).isEqualTo("用户不存在");
    }

    @Test
    @DisplayName("数据过期警告场景")
    void shouldHandleDataExpiredWarning() {
      // Arrange
      String expiredData = "2024-01-01 的数据";

      // Act
      R<String> response = R.warn("数据已过期，请刷新", expiredData);

      // Assert
      assertThat(response.getCode()).isEqualTo(HttpStatus.WARN);
      assertThat(R.isError(response)).isTrue();
      assertThat(response.getData()).contains("2024-01-01");
    }
  }

  /** 测试用户类. */
  static class TestUser {
    private String name;
    private int age;

    public TestUser(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }
  }
}
