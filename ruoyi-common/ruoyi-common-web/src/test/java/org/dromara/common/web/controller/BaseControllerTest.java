package org.dromara.common.web.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.dromara.common.core.domain.R;
import org.dromara.common.web.BaseUnitTest;
import org.dromara.common.web.core.BaseController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * BaseController (控制器基类) 单元测试.
 *
 * <p>用途: 提供toAjax()便捷方法将操作结果转换为统一响应 测试范围: toAjax(int)、toAjax(boolean)方法的各种场景
 *
 * @author Test Team
 */
@DisplayName("BaseController (控制器基类) 单元测试")
class BaseControllerTest extends BaseUnitTest {

  /** 测试用的BaseController实现. */
  private static class TestController extends BaseController {
    // 公开protected方法用于测试
    @Override
    public R<Void> toAjax(int rows) {
      return super.toAjax(rows);
    }

    @Override
    public R<Void> toAjax(boolean result) {
      return super.toAjax(result);
    }
  }

  private final TestController controller = new TestController();

  @Nested
  @DisplayName("1. toAjax(int rows) 测试")
  class ToAjaxIntTests {

    @Test
    @DisplayName("当影响行数为1时应该返回成功")
    void shouldReturnSuccessWhenRowsIsOne() {
      // Act
      R<Void> result = controller.toAjax(1);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(200);
      assertThat(R.isSuccess(result)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 5, 10, 100, 1000})
    @DisplayName("当影响行数大于0时应该返回成功")
    void shouldReturnSuccessWhenRowsIsPositive(int rows) {
      // Act
      R<Void> result = controller.toAjax(rows);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(200);
      assertThat(R.isSuccess(result)).isTrue();
    }

    @Test
    @DisplayName("当影响行数为0时应该返回失败")
    void shouldReturnFailWhenRowsIsZero() {
      // Act
      R<Void> result = controller.toAjax(0);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(500);
      assertThat(R.isSuccess(result)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, -100})
    @DisplayName("当影响行数为负数时应该返回失败")
    void shouldReturnFailWhenRowsIsNegative(int rows) {
      // Act
      R<Void> result = controller.toAjax(rows);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(500);
      assertThat(R.isSuccess(result)).isFalse();
    }

    @Test
    @DisplayName("当影响行数为Integer.MAX_VALUE时应该返回成功")
    void shouldReturnSuccessWhenRowsIsMaxValue() {
      // Act
      R<Void> result = controller.toAjax(Integer.MAX_VALUE);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(200);
      assertThat(R.isSuccess(result)).isTrue();
    }

    @Test
    @DisplayName("当影响行数为Integer.MIN_VALUE时应该返回失败")
    void shouldReturnFailWhenRowsIsMinValue() {
      // Act
      R<Void> result = controller.toAjax(Integer.MIN_VALUE);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(500);
      assertThat(R.isSuccess(result)).isFalse();
    }
  }

  @Nested
  @DisplayName("2. toAjax(boolean result) 测试")
  class ToAjaxBooleanTests {

    @Test
    @DisplayName("当布尔值为true时应该返回成功")
    void shouldReturnSuccessWhenTrue() {
      // Act
      R<Void> result = controller.toAjax(true);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(200);
      assertThat(R.isSuccess(result)).isTrue();
    }

    @Test
    @DisplayName("当布尔值为false时应该返回失败")
    void shouldReturnFailWhenFalse() {
      // Act
      R<Void> result = controller.toAjax(false);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo(500);
      assertThat(R.isSuccess(result)).isFalse();
    }
  }

  @Nested
  @DisplayName("3. 真实业务场景测试")
  class RealWorldScenarioTests {

    @Test
    @DisplayName("场景: 数据库更新操作 - 成功更新1条记录")
    void shouldHandleSuccessfulDatabaseUpdate() {
      // Arrange - 模拟数据库更新返回影响行数
      int affectedRows = 1;

      // Act
      R<Void> result = controller.toAjax(affectedRows);

      // Assert
      assertThat(R.isSuccess(result)).isTrue();
      assertThat(result.getMsg()).isEqualTo("操作成功");
    }

    @Test
    @DisplayName("场景: 数据库删除操作 - 未找到记录")
    void shouldHandleDatabaseDeleteWithNoRecords() {
      // Arrange - 模拟删除操作未找到记录
      int affectedRows = 0;

      // Act
      R<Void> result = controller.toAjax(affectedRows);

      // Assert
      assertThat(R.isSuccess(result)).isFalse();
      assertThat(result.getMsg()).isEqualTo("操作失败");
    }

    @Test
    @DisplayName("场景: 批量删除操作 - 成功删除多条")
    void shouldHandleBatchDeleteSuccess() {
      // Arrange - 模拟批量删除10条记录
      int affectedRows = 10;

      // Act
      R<Void> result = controller.toAjax(affectedRows);

      // Assert
      assertThat(R.isSuccess(result)).isTrue();
    }

    @Test
    @DisplayName("场景: 数据校验结果 - 校验通过")
    void shouldHandleValidationSuccess() {
      // Arrange - 模拟数据校验通过
      boolean validationResult = true;

      // Act
      R<Void> result = controller.toAjax(validationResult);

      // Assert
      assertThat(R.isSuccess(result)).isTrue();
    }

    @Test
    @DisplayName("场景: 数据校验结果 - 校验失败")
    void shouldHandleValidationFailure() {
      // Arrange - 模拟数据校验失败
      boolean validationResult = false;

      // Act
      R<Void> result = controller.toAjax(validationResult);

      // Assert
      assertThat(R.isSuccess(result)).isFalse();
    }

    @Test
    @DisplayName("场景: 业务逻辑判断 - 条件满足")
    void shouldHandleBusinessConditionMet() {
      // Arrange - 模拟业务条件满足
      boolean conditionMet = true;

      // Act
      R<Void> result = controller.toAjax(conditionMet);

      // Assert
      assertThat(R.isSuccess(result)).isTrue();
    }
  }

  @Nested
  @DisplayName("4. 响应对象验证测试")
  class ResponseObjectValidationTests {

    @Test
    @DisplayName("成功响应应该包含正确的状态码和消息")
    void successResponseShouldHaveCorrectCodeAndMessage() {
      // Act
      R<Void> result = controller.toAjax(1);

      // Assert
      assertThat(result.getCode()).isEqualTo(200);
      assertThat(result.getMsg()).isEqualTo("操作成功");
      assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("失败响应应该包含正确的状态码和消息")
    void failResponseShouldHaveCorrectCodeAndMessage() {
      // Act
      R<Void> result = controller.toAjax(0);

      // Assert
      assertThat(result.getCode()).isEqualTo(500);
      assertThat(result.getMsg()).isEqualTo("操作失败");
      assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("响应对象应该不为null")
    void responseShouldNotBeNull() {
      // Act
      R<Void> successResult = controller.toAjax(1);
      R<Void> failResult = controller.toAjax(0);

      // Assert
      assertThat(successResult).isNotNull();
      assertThat(failResult).isNotNull();
    }
  }

  @Nested
  @DisplayName("5. 方法重载测试")
  class MethodOverloadingTests {

    @Test
    @DisplayName("两个toAjax方法应该产生一致的结果 - 成功情况")
    void bothToAjaxMethodsShouldProduceConsistentSuccessResults() {
      // Act
      R<Void> intResult = controller.toAjax(1);
      R<Void> boolResult = controller.toAjax(true);

      // Assert
      assertThat(intResult.getCode()).isEqualTo(boolResult.getCode());
      assertThat(intResult.getMsg()).isEqualTo(boolResult.getMsg());
      assertThat(R.isSuccess(intResult)).isEqualTo(R.isSuccess(boolResult));
    }

    @Test
    @DisplayName("两个toAjax方法应该产生一致的结果 - 失败情况")
    void bothToAjaxMethodsShouldProduceConsistentFailResults() {
      // Act
      R<Void> intResult = controller.toAjax(0);
      R<Void> boolResult = controller.toAjax(false);

      // Assert
      assertThat(intResult.getCode()).isEqualTo(boolResult.getCode());
      assertThat(intResult.getMsg()).isEqualTo(boolResult.getMsg());
      assertThat(R.isSuccess(intResult)).isEqualTo(R.isSuccess(boolResult));
    }
  }
}
