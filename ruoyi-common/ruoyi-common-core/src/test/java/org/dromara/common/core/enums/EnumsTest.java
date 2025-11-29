package org.dromara.common.core.enums;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.dromara.common.core.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * 枚举类综合单元测试 包含 DeviceType, UserStatus, FormatsType, UserType, LoginType.
 *
 * @author Test Team
 */
@DisplayName("枚举类综合单元测试")
class EnumsTest extends BaseUnitTest {

  @Nested
  @DisplayName("DeviceType (设备类型枚举) 测试")
  class DeviceTypeTests {

    @Test
    @DisplayName("应该定义4个设备类型常量")
    void shouldDefineFourDeviceTypes() {
      assertThat(DeviceType.values()).hasSize(4);
    }

    @Test
    @DisplayName("PC - 电脑端")
    void shouldDefinePcDevice() {
      assertThat(DeviceType.PC.getDevice()).isEqualTo("pc");
    }

    @Test
    @DisplayName("APP - 移动应用端")
    void shouldDefineAppDevice() {
      assertThat(DeviceType.APP.getDevice()).isEqualTo("app");
    }

    @Test
    @DisplayName("XCX - 小程序端")
    void shouldDefineXcxDevice() {
      assertThat(DeviceType.XCX.getDevice()).isEqualTo("xcx");
    }

    @Test
    @DisplayName("SOCIAL - 第三方社交登录")
    void shouldDefineSocialDevice() {
      assertThat(DeviceType.SOCIAL.getDevice()).isEqualTo("social");
    }

    @Test
    @DisplayName("应该可以通过 valueOf() 获取枚举")
    void shouldGetEnumByValueOf() {
      assertThat(DeviceType.valueOf("PC")).isEqualTo(DeviceType.PC);
      assertThat(DeviceType.valueOf("APP")).isEqualTo(DeviceType.APP);
    }

    @Test
    @DisplayName("应该可以通过 values() 获取所有枚举")
    void shouldGetAllValues() {
      DeviceType[] values = DeviceType.values();

      assertThat(values)
          .containsExactly(DeviceType.PC, DeviceType.APP, DeviceType.XCX, DeviceType.SOCIAL);
    }
  }

  @Nested
  @DisplayName("UserStatus (用户状态枚举) 测试")
  class UserStatusTests {

    @Test
    @DisplayName("应该定义3个用户状态常量")
    void shouldDefineThreeUserStatus() {
      assertThat(UserStatus.values()).hasSize(3);
    }

    @Test
    @DisplayName("OK - 正常状态")
    void shouldDefineOkStatus() {
      assertThat(UserStatus.OK.getCode()).isEqualTo("0");
      assertThat(UserStatus.OK.getInfo()).isEqualTo("正常");
    }

    @Test
    @DisplayName("DISABLE - 停用状态")
    void shouldDefineDisableStatus() {
      assertThat(UserStatus.DISABLE.getCode()).isEqualTo("1");
      assertThat(UserStatus.DISABLE.getInfo()).isEqualTo("停用");
    }

    @Test
    @DisplayName("DELETED - 删除状态")
    void shouldDefineDeletedStatus() {
      assertThat(UserStatus.DELETED.getCode()).isEqualTo("2");
      assertThat(UserStatus.DELETED.getInfo()).isEqualTo("删除");
    }

    @Test
    @DisplayName("应该可以通过状态码识别用户状态")
    void shouldIdentifyUserStatusByCode() {
      for (UserStatus status : UserStatus.values()) {
        assertThat(status.getCode()).isIn("0", "1", "2");
      }
    }

    @Test
    @DisplayName("Lombok getter - 应该正确获取字段值")
    void shouldGetFieldsUsingGetter() {
      assertThat(UserStatus.OK.getCode()).isNotBlank();
      assertThat(UserStatus.OK.getInfo()).isNotBlank();
    }
  }

  @Nested
  @DisplayName("FormatsType (日期时间格式枚举) 测试")
  class FormatsTypeTests {

    @Test
    @DisplayName("应该定义20个日期时间格式常量")
    void shouldDefineTwentyFormats() {
      assertThat(FormatsType.values()).hasSize(20);
    }

    @ParameterizedTest
    @CsvSource({
      "YY, yy",
      "YYYY, yyyy",
      "YYYY_MM, yyyy-MM",
      "YYYY_MM_DD, yyyy-MM-dd",
      "YYYY_MM_DD_HH_MM_SS, yyyy-MM-dd HH:mm:ss",
      "HH_MM_SS, HH:mm:ss"
    })
    @DisplayName("基本日期时间格式验证")
    void shouldDefineBasicFormats(String enumName, String format) {
      FormatsType formatsType = FormatsType.valueOf(enumName);

      assertThat(formatsType.getTimeFormat()).isEqualTo(format);
    }

    @ParameterizedTest
    @CsvSource({
      "YYYY_MM_SLASH, yyyy/MM",
      "YYYY_MM_DD_SLASH, yyyy/MM/dd",
      "YYYY_MM_DD_HH_MM_SS_SLASH, yyyy/MM/dd HH:mm:ss"
    })
    @DisplayName("斜杠分隔格式验证")
    void shouldDefineSlashFormats(String enumName, String format) {
      FormatsType formatsType = FormatsType.valueOf(enumName);

      assertThat(formatsType.getTimeFormat()).isEqualTo(format);
    }

    @ParameterizedTest
    @CsvSource({
      "YYYY_MM_DOT, yyyy.MM",
      "YYYY_MM_DD_DOT, yyyy.MM.dd",
      "YYYY_MM_DD_HH_MM_SS_DOT, yyyy.MM.dd HH:mm:ss"
    })
    @DisplayName("点分隔格式验证")
    void shouldDefineDotFormats(String enumName, String format) {
      FormatsType formatsType = FormatsType.valueOf(enumName);

      assertThat(formatsType.getTimeFormat()).isEqualTo(format);
    }

    @ParameterizedTest
    @CsvSource({"YYYYMM, yyyyMM", "YYYYMMDD, yyyyMMdd", "YYYYMMDDHHMMSS, yyyyMMddHHmmss"})
    @DisplayName("无分隔符格式验证")
    void shouldDefineCompactFormats(String enumName, String format) {
      FormatsType formatsType = FormatsType.valueOf(enumName);

      assertThat(formatsType.getTimeFormat()).isEqualTo(format);
    }

    @Test
    @DisplayName("getFormatsType() - 应该根据字符串找到匹配的格式")
    void shouldFindFormatsTypeByString() {
      // getFormatsType 使用 contains(str, format)，检查输入字符串是否包含枚举的格式
      // 它会返回第一个枚举值使得 str.contains(enum.format) 为 true

      // 测试包含特定格式的字符串
      FormatsType result1 = FormatsType.getFormatsType("yyyyMMddHHmmss");
      // yyyyMMddHHmmss 应该能找到匹配
      assertThat(result1).isNotNull();
      assertThat("yyyyMMddHHmmss").contains(result1.getTimeFormat());

      FormatsType result2 = FormatsType.getFormatsType("yyyy/MM/dd");
      // yyyy/MM/dd 应该能找到匹配
      assertThat(result2).isNotNull();
      assertThat("yyyy/MM/dd").contains(result2.getTimeFormat());

      FormatsType result3 = FormatsType.getFormatsType("HH:mm:ss");
      // HH:mm:ss 应该能找到匹配
      assertThat(result3).isNotNull();
      assertThat("HH:mm:ss").contains(result3.getTimeFormat());
    }

    @Test
    @DisplayName("getFormatsType() - 应该对包含格式的字符串返回第一个匹配的格式")
    void shouldFindFirstMatchingFormat() {
      // getFormatsType 方法使用 contains 检查，返回第一个匹配的格式
      FormatsType result = FormatsType.getFormatsType("HH:mm:ss");
      // HH:mm:ss 格式应该匹配
      assertThat(result).isNotNull();
      assertThat(result.getTimeFormat()).isEqualTo("HH:mm:ss");
    }

    @Test
    @DisplayName("getFormatsType() - 应该对无法匹配的字符串抛出异常")
    void shouldThrowExceptionForUnmatchedString() {
      assertThatThrownBy(() -> FormatsType.getFormatsType("invalid format"))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("'FormatsType' not found By");
    }

    @Test
    @DisplayName("getter - 应该返回正确的时间格式")
    void shouldGetTimeFormat() {
      assertThat(FormatsType.YYYY_MM_DD.getTimeFormat()).isEqualTo("yyyy-MM-dd");
      assertThat(FormatsType.YYYY_MM_DD_HH_MM_SS.getTimeFormat()).isEqualTo("yyyy-MM-dd HH:mm:ss");
    }
  }

  @Nested
  @DisplayName("UserType (用户类型枚举) 测试")
  class UserTypeTests {

    @Test
    @DisplayName("应该定义2个用户类型常量")
    void shouldDefineTwoUserTypes() {
      assertThat(UserType.values()).hasSize(2);
    }

    @Test
    @DisplayName("SYS_USER - 后台系统用户")
    void shouldDefineSysUser() {
      assertThat(UserType.SYS_USER.getUserType()).isEqualTo("sys_user");
    }

    @Test
    @DisplayName("APP_USER - 移动客户端用户")
    void shouldDefineAppUser() {
      assertThat(UserType.APP_USER.getUserType()).isEqualTo("app_user");
    }

    @Test
    @DisplayName("getUserType() - 应该根据字符串找到匹配的用户类型")
    void shouldFindUserTypeByString() {
      UserType result1 = UserType.getUserType("sys_user");
      assertThat(result1).isEqualTo(UserType.SYS_USER);

      UserType result2 = UserType.getUserType("app_user");
      assertThat(result2).isEqualTo(UserType.APP_USER);
    }

    @Test
    @DisplayName("getUserType() - 应该对包含用户类型的字符串返回正确类型")
    void shouldFindUserTypeInString() {
      UserType result = UserType.getUserType("token:sys_user:123");

      assertThat(result).isEqualTo(UserType.SYS_USER);
    }

    @Test
    @DisplayName("getUserType() - 应该对无法匹配的字符串抛出异常")
    void shouldThrowExceptionForUnmatchedString() {
      assertThatThrownBy(() -> UserType.getUserType("invalid_user_type"))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("'UserType' not found By");
    }

    @Test
    @DisplayName("应该可以通过 valueOf() 获取枚举")
    void shouldGetEnumByValueOf() {
      assertThat(UserType.valueOf("SYS_USER")).isEqualTo(UserType.SYS_USER);
      assertThat(UserType.valueOf("APP_USER")).isEqualTo(UserType.APP_USER);
    }
  }

  @Nested
  @DisplayName("LoginType (登录类型枚举) 测试")
  class LoginTypeTests {

    @Test
    @DisplayName("应该定义4个登录类型常量")
    void shouldDefineFourLoginTypes() {
      assertThat(LoginType.values()).hasSize(4);
    }

    @Test
    @DisplayName("PASSWORD - 密码登录")
    void shouldDefinePasswordLogin() {
      assertThat(LoginType.PASSWORD.getRetryLimitExceed())
          .isEqualTo("user.password.retry.limit.exceed");
      assertThat(LoginType.PASSWORD.getRetryLimitCount())
          .isEqualTo("user.password.retry.limit.count");
    }

    @Test
    @DisplayName("SMS - 短信登录")
    void shouldDefineSmsLogin() {
      assertThat(LoginType.SMS.getRetryLimitExceed()).isEqualTo("sms.code.retry.limit.exceed");
      assertThat(LoginType.SMS.getRetryLimitCount()).isEqualTo("sms.code.retry.limit.count");
    }

    @Test
    @DisplayName("EMAIL - 邮箱登录")
    void shouldDefineEmailLogin() {
      assertThat(LoginType.EMAIL.getRetryLimitExceed()).isEqualTo("email.code.retry.limit.exceed");
      assertThat(LoginType.EMAIL.getRetryLimitCount()).isEqualTo("email.code.retry.limit.count");
    }

    @Test
    @DisplayName("XCX - 小程序登录")
    void shouldDefineXcxLogin() {
      assertThat(LoginType.XCX.getRetryLimitExceed()).isEmpty();
      assertThat(LoginType.XCX.getRetryLimitCount()).isEmpty();
    }

    @Test
    @DisplayName("应该可以通过 values() 获取所有登录类型")
    void shouldGetAllLoginTypes() {
      LoginType[] values = LoginType.values();

      assertThat(values)
          .containsExactly(LoginType.PASSWORD, LoginType.SMS, LoginType.EMAIL, LoginType.XCX);
    }

    @Test
    @DisplayName("getter - 应该正确获取重试限制字段")
    void shouldGetRetryLimitFields() {
      assertThat(LoginType.PASSWORD.getRetryLimitExceed()).isNotBlank();
      assertThat(LoginType.PASSWORD.getRetryLimitCount()).isNotBlank();
    }

    @Test
    @DisplayName("应该可以通过 valueOf() 获取枚举")
    void shouldGetEnumByValueOf() {
      assertThat(LoginType.valueOf("PASSWORD")).isEqualTo(LoginType.PASSWORD);
      assertThat(LoginType.valueOf("SMS")).isEqualTo(LoginType.SMS);
      assertThat(LoginType.valueOf("EMAIL")).isEqualTo(LoginType.EMAIL);
      assertThat(LoginType.valueOf("XCX")).isEqualTo(LoginType.XCX);
    }
  }
}
