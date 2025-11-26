package org.dromara.common.core.enums;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.dromara.common.core.BaseUnitTest;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * BusinessStatusEnum 单元测试
 *
 * @author Test Team
 */
@DisplayName("BusinessStatusEnum (业务状态枚举) 单元测试")
class BusinessStatusEnumTest extends BaseUnitTest {

    @Nested
    @DisplayName("1. 枚举常量测试")
    class EnumConstantsTests {

        @Test
        @DisplayName("应该定义7个业务状态常量")
        void shouldDefineSevenStatusConstants() {
            assertThat(BusinessStatusEnum.values()).hasSize(7);
        }

        @Test
        @DisplayName("CANCEL - 已撤销状态")
        void shouldDefineCancelStatus() {
            assertThat(BusinessStatusEnum.CANCEL.getStatus()).isEqualTo("cancel");
            assertThat(BusinessStatusEnum.CANCEL.getDesc()).isEqualTo("已撤销");
        }

        @Test
        @DisplayName("DRAFT - 草稿状态")
        void shouldDefineDraftStatus() {
            assertThat(BusinessStatusEnum.DRAFT.getStatus()).isEqualTo("draft");
            assertThat(BusinessStatusEnum.DRAFT.getDesc()).isEqualTo("草稿");
        }

        @Test
        @DisplayName("WAITING - 待审核状态")
        void shouldDefineWaitingStatus() {
            assertThat(BusinessStatusEnum.WAITING.getStatus()).isEqualTo("waiting");
            assertThat(BusinessStatusEnum.WAITING.getDesc()).isEqualTo("待审核");
        }

        @Test
        @DisplayName("FINISH - 已完成状态")
        void shouldDefineFinishStatus() {
            assertThat(BusinessStatusEnum.FINISH.getStatus()).isEqualTo("finish");
            assertThat(BusinessStatusEnum.FINISH.getDesc()).isEqualTo("已完成");
        }

        @Test
        @DisplayName("INVALID - 已作废状态")
        void shouldDefineInvalidStatus() {
            assertThat(BusinessStatusEnum.INVALID.getStatus()).isEqualTo("invalid");
            assertThat(BusinessStatusEnum.INVALID.getDesc()).isEqualTo("已作废");
        }

        @Test
        @DisplayName("BACK - 已退回状态")
        void shouldDefineBackStatus() {
            assertThat(BusinessStatusEnum.BACK.getStatus()).isEqualTo("back");
            assertThat(BusinessStatusEnum.BACK.getDesc()).isEqualTo("已退回");
        }

        @Test
        @DisplayName("TERMINATION - 已终止状态")
        void shouldDefineTerminationStatus() {
            assertThat(BusinessStatusEnum.TERMINATION.getStatus()).isEqualTo("termination");
            assertThat(BusinessStatusEnum.TERMINATION.getDesc()).isEqualTo("已终止");
        }
    }

    @Nested
    @DisplayName("2. getByStatus() 方法测试")
    class GetByStatusTests {

        @ParameterizedTest
        @ValueSource(
                strings = {
                    "cancel",
                    "draft",
                    "waiting",
                    "finish",
                    "invalid",
                    "back",
                    "termination"
                })
        @DisplayName("应该根据状态码返回对应的枚举")
        void shouldReturnEnumByStatus(String status) {
            BusinessStatusEnum result = BusinessStatusEnum.getByStatus(status);

            assertThat(result).isNotNull();
            assertThat(result.getStatus()).isEqualTo(status);
        }

        @Test
        @DisplayName("应该对无效状态返回 null")
        void shouldReturnNullForInvalidStatus() {
            BusinessStatusEnum result = BusinessStatusEnum.getByStatus("invalid_status");

            assertThat(result).isNull();
        }

        @Test
        @DisplayName("应该对 null 状态抛出 NullPointerException")
        void shouldThrowNullPointerExceptionForNullStatus() {
            // ConcurrentHashMap 不允许 null key，会抛出 NullPointerException
            assertThatThrownBy(() -> BusinessStatusEnum.getByStatus(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("3. findByStatus() 方法测试")
    class FindByStatusTests {

        @Test
        @DisplayName("应该根据状态码返回对应的描述")
        void shouldReturnDescriptionByStatus() {
            assertThat(BusinessStatusEnum.findByStatus("draft")).isEqualTo("草稿");
            assertThat(BusinessStatusEnum.findByStatus("waiting")).isEqualTo("待审核");
            assertThat(BusinessStatusEnum.findByStatus("finish")).isEqualTo("已完成");
        }

        @Test
        @DisplayName("应该对无效状态返回空字符串")
        void shouldReturnEmptyStringForInvalidStatus() {
            assertThat(BusinessStatusEnum.findByStatus("invalid_status")).isEmpty();
        }

        @Test
        @DisplayName("应该对 null 状态返回空字符串")
        void shouldReturnEmptyStringForNullStatus() {
            assertThat(BusinessStatusEnum.findByStatus(null)).isEmpty();
        }

        @Test
        @DisplayName("应该对空字符串状态返回空字符串")
        void shouldReturnEmptyStringForBlankStatus() {
            assertThat(BusinessStatusEnum.findByStatus("")).isEmpty();
            assertThat(BusinessStatusEnum.findByStatus("   ")).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. isDraftOrCancelOrBack() 方法测试")
    class IsDraftOrCancelOrBackTests {

        @ParameterizedTest
        @ValueSource(strings = {"draft", "cancel", "back"})
        @DisplayName("应该对草稿、已撤销、已退回状态返回 true")
        void shouldReturnTrueForDraftCancelBack(String status) {
            assertThat(BusinessStatusEnum.isDraftOrCancelOrBack(status)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"waiting", "finish", "invalid", "termination"})
        @DisplayName("应该对其他状态返回 false")
        void shouldReturnFalseForOtherStatus(String status) {
            assertThat(BusinessStatusEnum.isDraftOrCancelOrBack(status)).isFalse();
        }

        @Test
        @DisplayName("应该对 null 状态返回 false")
        void shouldReturnFalseForNullStatus() {
            assertThat(BusinessStatusEnum.isDraftOrCancelOrBack(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("5. initialState() 方法测试")
    class InitialStateTests {

        @ParameterizedTest
        @ValueSource(strings = {"cancel", "back", "invalid", "termination"})
        @DisplayName("应该对撤销、退回、作废、终止状态返回 true")
        void shouldReturnTrueForInitialStates(String status) {
            assertThat(BusinessStatusEnum.initialState(status)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"draft", "waiting", "finish"})
        @DisplayName("应该对其他状态返回 false")
        void shouldReturnFalseForNonInitialStates(String status) {
            assertThat(BusinessStatusEnum.initialState(status)).isFalse();
        }
    }

    @Nested
    @DisplayName("6. runningStatus() 方法测试")
    class RunningStatusTests {

        @Test
        @DisplayName("应该返回运行中的状态列表")
        void shouldReturnRunningStatusList() {
            List<String> runningStatus = BusinessStatusEnum.runningStatus();

            assertThat(runningStatus)
                    .hasSize(4)
                    .containsExactly("draft", "waiting", "back", "cancel");
        }

        @Test
        @DisplayName("返回的列表应该是不可变的")
        void shouldReturnImmutableList() {
            List<String> runningStatus = BusinessStatusEnum.runningStatus();

            assertThatThrownBy(() -> runningStatus.add("new_status"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("7. finishStatus() 方法测试")
    class FinishStatusTests {

        @Test
        @DisplayName("应该返回结束状态列表")
        void shouldReturnFinishStatusList() {
            List<String> finishStatus = BusinessStatusEnum.finishStatus();

            assertThat(finishStatus).hasSize(3).containsExactly("finish", "invalid", "termination");
        }

        @Test
        @DisplayName("返回的列表应该是不可变的")
        void shouldReturnImmutableList() {
            List<String> finishStatus = BusinessStatusEnum.finishStatus();

            assertThatThrownBy(() -> finishStatus.add("new_status"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("8. checkStartStatus() 启动流程校验测试")
    class CheckStartStatusTests {

        @ParameterizedTest
        @ValueSource(strings = {"draft", "cancel", "back"})
        @DisplayName("应该允许草稿、已撤销、已退回状态启动流程")
        void shouldAllowStartForDraftCancelBack(String status) {
            assertThatCode(() -> BusinessStatusEnum.checkStartStatus(status))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该拒绝待审核状态启动流程")
        void shouldRejectStartForWaitingStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkStartStatus("waiting"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已提交过申请,正在审批中！");
        }

        @Test
        @DisplayName("应该拒绝已完成状态启动流程")
        void shouldRejectStartForFinishStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkStartStatus("finish"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已完成申请！");
        }

        @Test
        @DisplayName("应该拒绝已作废状态启动流程")
        void shouldRejectStartForInvalidStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkStartStatus("invalid"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已作废！");
        }

        @Test
        @DisplayName("应该拒绝已终止状态启动流程")
        void shouldRejectStartForTerminationStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkStartStatus("termination"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已终止！");
        }

        @Test
        @DisplayName("应该拒绝空状态启动流程")
        void shouldRejectStartForBlankStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkStartStatus(""))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("流程状态为空！");

            assertThatThrownBy(() -> BusinessStatusEnum.checkStartStatus(null))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("流程状态为空！");
        }
    }

    @Nested
    @DisplayName("9. checkCancelStatus() 撤销流程校验测试")
    class CheckCancelStatusTests {

        @ParameterizedTest
        @ValueSource(strings = {"draft", "waiting"})
        @DisplayName("应该允许草稿、待审核状态撤销流程")
        void shouldAllowCancelForDraftWaiting(String status) {
            assertThatCode(() -> BusinessStatusEnum.checkCancelStatus(status))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该拒绝已撤销状态再次撤销")
        void shouldRejectCancelForCancelStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkCancelStatus("cancel"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已撤销！");
        }

        @Test
        @DisplayName("应该拒绝已完成状态撤销")
        void shouldRejectCancelForFinishStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkCancelStatus("finish"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已完成申请！");
        }

        @Test
        @DisplayName("应该拒绝已作废状态撤销")
        void shouldRejectCancelForInvalidStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkCancelStatus("invalid"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已作废！");
        }

        @Test
        @DisplayName("应该拒绝已终止状态撤销")
        void shouldRejectCancelForTerminationStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkCancelStatus("termination"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已终止！");
        }

        @Test
        @DisplayName("应该拒绝已退回状态撤销")
        void shouldRejectCancelForBackStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkCancelStatus("back"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已退回！");
        }
    }

    @Nested
    @DisplayName("10. checkBackStatus() 驳回流程校验测试")
    class CheckBackStatusTests {

        @ParameterizedTest
        @ValueSource(strings = {"draft", "waiting"})
        @DisplayName("应该允许草稿、待审核状态驳回")
        void shouldAllowBackForDraftWaiting(String status) {
            assertThatCode(() -> BusinessStatusEnum.checkBackStatus(status))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该拒绝已退回状态再次驳回")
        void shouldRejectBackForBackStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkBackStatus("back"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已退回！");
        }

        @Test
        @DisplayName("应该拒绝已完成状态驳回")
        void shouldRejectBackForFinishStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkBackStatus("finish"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已完成申请！");
        }

        @Test
        @DisplayName("应该拒绝已作废状态驳回")
        void shouldRejectBackForInvalidStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkBackStatus("invalid"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已作废！");
        }

        @Test
        @DisplayName("应该拒绝已终止状态驳回")
        void shouldRejectBackForTerminationStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkBackStatus("termination"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已终止！");
        }

        @Test
        @DisplayName("应该拒绝已撤销状态驳回")
        void shouldRejectBackForCancelStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkBackStatus("cancel"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已撤销！");
        }
    }

    @Nested
    @DisplayName("11. checkInvalidStatus() 作废/终止流程校验测试")
    class CheckInvalidStatusTests {

        @ParameterizedTest
        @ValueSource(strings = {"draft", "waiting", "cancel", "back"})
        @DisplayName("应该允许草稿、待审核、已撤销、已退回状态作废")
        void shouldAllowInvalidForNonFinishedStatus(String status) {
            assertThatCode(() -> BusinessStatusEnum.checkInvalidStatus(status))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("应该拒绝已完成状态作废")
        void shouldRejectInvalidForFinishStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkInvalidStatus("finish"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已完成申请！");
        }

        @Test
        @DisplayName("应该拒绝已作废状态再次作废")
        void shouldRejectInvalidForInvalidStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkInvalidStatus("invalid"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已作废！");
        }

        @Test
        @DisplayName("应该拒绝已终止状态再次作废")
        void shouldRejectInvalidForTerminationStatus() {
            assertThatThrownBy(() -> BusinessStatusEnum.checkInvalidStatus("termination"))
                    .isInstanceOf(ServiceException.class)
                    .hasMessage("该单据已终止！");
        }
    }

    @Nested
    @DisplayName("12. Lombok 生成方法测试")
    class LombokGeneratedMethodsTests {

        @Test
        @DisplayName("getter - 应该正确获取字段值")
        void shouldGetFieldValues() {
            BusinessStatusEnum draft = BusinessStatusEnum.DRAFT;

            assertThat(draft.getStatus()).isEqualTo("draft");
            assertThat(draft.getDesc()).isEqualTo("草稿");
        }

        @Test
        @DisplayName("应该可以通过 values() 获取所有枚举常量")
        void shouldGetAllValues() {
            BusinessStatusEnum[] values = BusinessStatusEnum.values();

            assertThat(values)
                    .contains(
                            BusinessStatusEnum.CANCEL,
                            BusinessStatusEnum.DRAFT,
                            BusinessStatusEnum.WAITING,
                            BusinessStatusEnum.FINISH,
                            BusinessStatusEnum.INVALID,
                            BusinessStatusEnum.BACK,
                            BusinessStatusEnum.TERMINATION);
        }

        @Test
        @DisplayName("应该可以通过 valueOf() 获取枚举常量")
        void shouldGetValueOf() {
            BusinessStatusEnum draft = BusinessStatusEnum.valueOf("DRAFT");

            assertThat(draft).isEqualTo(BusinessStatusEnum.DRAFT);
            assertThat(draft.getStatus()).isEqualTo("draft");
        }
    }
}
