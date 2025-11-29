package org.dromara.common.sensitive.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;
import org.dromara.common.sensitive.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * SensitiveStrategy (脱敏策略) 单元测试.
 *
 * <p>用途: 提供各种数据脱敏策略，用于JSON序列化时的数据保护 核心功能: 15种脱敏策略（身份证、手机号、地址、邮箱等）
 *
 * @author Test Team
 */
@DisplayName("SensitiveStrategy (脱敏策略) 单元测试")
class SensitiveStrategyTest extends BaseUnitTest {

  @Nested
  @DisplayName("1. ID_CARD (身份证) 脱敏测试")
  class IdCardTests {

    @Test
    @DisplayName("应该脱敏18位身份证号")
    void shouldDesensitize18DigitIdCard() {
      // Arrange
      String idCard = "110101199003071234";
      Function<String, String> desensitizer = SensitiveStrategy.ID_CARD.desensitizer();

      // Act
      String result = desensitizer.apply(idCard);

      // Assert
      assertThat(result).isNotEqualTo(idCard);
      assertThat(result).startsWith("110"); // 前3位可见
      assertThat(result).endsWith("1234"); // 后4位可见
      assertThat(result).contains("*"); // 中间有星号
    }

    @Test
    @DisplayName("应该脱敏15位身份证号")
    void shouldDesensitize15DigitIdCard() {
      // Arrange
      String idCard = "110101900307123";
      Function<String, String> desensitizer = SensitiveStrategy.ID_CARD.desensitizer();

      // Act
      String result = desensitizer.apply(idCard);

      // Assert
      assertThat(result).isNotEqualTo(idCard);
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理空字符串")
    void shouldHandleEmptyString() {
      // Arrange
      Function<String, String> desensitizer = SensitiveStrategy.ID_CARD.desensitizer();

      // Act
      String result = desensitizer.apply("");

      // Assert
      assertThat(result).isEmpty();
    }
  }

  @Nested
  @DisplayName("2. PHONE (手机号) 脱敏测试")
  class PhoneTests {

    @Test
    @DisplayName("应该脱敏手机号中间4位")
    void shouldDesensitizeMobilePhone() {
      // Arrange
      String phone = "13800138000";
      Function<String, String> desensitizer = SensitiveStrategy.PHONE.desensitizer();

      // Act
      String result = desensitizer.apply(phone);

      // Assert
      assertThat(result).isNotEqualTo(phone);
      assertThat(result).startsWith("138"); // 前3位可见
      assertThat(result).endsWith("8000"); // 后4位可见
      assertThat(result).contains("****"); // 中间4位星号
    }

    @Test
    @DisplayName("应该处理不同格式的手机号")
    void shouldHandleDifferentPhoneFormats() {
      // Arrange
      String phone1 = "13912345678";
      String phone2 = "18612345678";
      Function<String, String> desensitizer = SensitiveStrategy.PHONE.desensitizer();

      // Act
      String result1 = desensitizer.apply(phone1);
      String result2 = desensitizer.apply(phone2);

      // Assert
      assertThat(result1).contains("****");
      assertThat(result2).contains("****");
    }
  }

  @Nested
  @DisplayName("3. ADDRESS (地址) 脱敏测试")
  class AddressTests {

    @Test
    @DisplayName("应该脱敏地址（保留前8个字符）")
    void shouldDesensitizeAddress() {
      // Arrange
      String address = "北京市朝阳区某某街道某某小区1号楼1单元101室";
      Function<String, String> desensitizer = SensitiveStrategy.ADDRESS.desensitizer();

      // Act
      String result = desensitizer.apply(address);

      // Assert
      assertThat(result).isNotEqualTo(address);
      assertThat(result).startsWith("北京市朝阳区"); // 前8个字符可见
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理短地址")
    void shouldHandleShortAddress() {
      // Arrange
      String shortAddress = "北京";
      Function<String, String> desensitizer = SensitiveStrategy.ADDRESS.desensitizer();

      // Act
      String result = desensitizer.apply(shortAddress);

      // Assert
      assertThat(result).isNotNull();
    }
  }

  @Nested
  @DisplayName("4. EMAIL (邮箱) 脱敏测试")
  class EmailTests {

    @Test
    @DisplayName("应该脱敏邮箱地址")
    void shouldDesensitizeEmail() {
      // Arrange
      String email = "example@domain.com";
      Function<String, String> desensitizer = SensitiveStrategy.EMAIL.desensitizer();

      // Act
      String result = desensitizer.apply(email);

      // Assert
      assertThat(result).isNotEqualTo(email);
      assertThat(result).contains("@"); // 保留@符号
      assertThat(result).contains("*"); // 有星号
      assertThat(result).endsWith(".com"); // 保留域名后缀
    }

    @Test
    @DisplayName("应该处理长用户名邮箱")
    void shouldHandleLongUsernameEmail() {
      // Arrange
      String email = "verylongusername@example.com";
      Function<String, String> desensitizer = SensitiveStrategy.EMAIL.desensitizer();

      // Act
      String result = desensitizer.apply(email);

      // Assert
      assertThat(result).contains("@");
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理短用户名邮箱")
    void shouldHandleShortUsernameEmail() {
      // Arrange
      String email = "ab@test.com";
      Function<String, String> desensitizer = SensitiveStrategy.EMAIL.desensitizer();

      // Act
      String result = desensitizer.apply(email);

      // Assert
      assertThat(result).contains("@");
    }
  }

  @Nested
  @DisplayName("5. BANK_CARD (银行卡) 脱敏测试")
  class BankCardTests {

    @Test
    @DisplayName("应该脱敏银行卡号")
    void shouldDesensitizeBankCard() {
      // Arrange
      String bankCard = "6217000010012345678"; // 19位银行卡号
      Function<String, String> desensitizer = SensitiveStrategy.BANK_CARD.desensitizer();

      // Act
      String result = desensitizer.apply(bankCard);

      // Assert
      assertThat(result).isNotEqualTo(bankCard);
      assertThat(result).contains("*");
      // Hutool的bankCard脱敏规则：保留前几位和后几位，中间星号
      assertThat(result.length()).isGreaterThanOrEqualTo(bankCard.length());
    }

    @Test
    @DisplayName("应该处理16位银行卡号")
    void shouldHandle16DigitBankCard() {
      // Arrange
      String bankCard = "6217000010012345";
      Function<String, String> desensitizer = SensitiveStrategy.BANK_CARD.desensitizer();

      // Act
      String result = desensitizer.apply(bankCard);

      // Assert
      assertThat(result).contains("*");
    }
  }

  @Nested
  @DisplayName("6. CHINESE_NAME (中文名) 脱敏测试")
  class ChineseNameTests {

    @Test
    @DisplayName("应该脱敏两字姓名")
    void shouldDesensitizeTwoCharName() {
      // Arrange
      String name = "张三";
      Function<String, String> desensitizer = SensitiveStrategy.CHINESE_NAME.desensitizer();

      // Act
      String result = desensitizer.apply(name);

      // Assert
      assertThat(result).isNotEqualTo(name);
      assertThat(result).startsWith("张");
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该脱敏三字姓名")
    void shouldDesensitizeThreeCharName() {
      // Arrange
      String name = "欧阳锋";
      Function<String, String> desensitizer = SensitiveStrategy.CHINESE_NAME.desensitizer();

      // Act
      String result = desensitizer.apply(name);

      // Assert
      assertThat(result).isNotEqualTo(name);
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该脱敏四字姓名")
    void shouldDesensitizeFourCharName() {
      // Arrange
      String name = "诸葛孔明";
      Function<String, String> desensitizer = SensitiveStrategy.CHINESE_NAME.desensitizer();

      // Act
      String result = desensitizer.apply(name);

      // Assert
      assertThat(result).isNotEqualTo(name);
      assertThat(result).contains("*");
    }
  }

  @Nested
  @DisplayName("7. FIXED_PHONE (固定电话) 脱敏测试")
  class FixedPhoneTests {

    @Test
    @DisplayName("应该脱敏固定电话")
    void shouldDesensitizeFixedPhone() {
      // Arrange
      String phone = "010-12345678";
      Function<String, String> desensitizer = SensitiveStrategy.FIXED_PHONE.desensitizer();

      // Act
      String result = desensitizer.apply(phone);

      // Assert
      assertThat(result).isNotEqualTo(phone);
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理不同区号的固定电话")
    void shouldHandleDifferentAreaCodes() {
      // Arrange
      String phone1 = "021-87654321"; // 上海
      String phone2 = "0755-12345678"; // 深圳
      Function<String, String> desensitizer = SensitiveStrategy.FIXED_PHONE.desensitizer();

      // Act
      String result1 = desensitizer.apply(phone1);
      String result2 = desensitizer.apply(phone2);

      // Assert
      assertThat(result1).contains("*");
      assertThat(result2).contains("*");
    }
  }

  @Nested
  @DisplayName("8. USER_ID (用户ID) 脱敏测试")
  class UserIdTests {

    @Test
    @DisplayName("应该生成随机用户ID")
    void shouldGenerateRandomUserId() {
      // Arrange
      Function<String, String> desensitizer = SensitiveStrategy.USER_ID.desensitizer();

      // Act
      String result = desensitizer.apply("anyInput");

      // Assert
      assertThat(result).isNotNull();
      assertThat(result).isNotEmpty();
      // USER_ID返回固定的随机ID格式
    }
  }

  @Nested
  @DisplayName("9. PASSWORD (密码) 脱敏测试")
  class PasswordTests {

    @Test
    @DisplayName("应该完全脱敏密码")
    void shouldCompletelyDesensitizePassword() {
      // Arrange
      String password = "MyP@ssw0rd123";
      Function<String, String> desensitizer = SensitiveStrategy.PASSWORD.desensitizer();

      // Act
      String result = desensitizer.apply(password);

      // Assert
      assertThat(result).isNotEqualTo(password);
      assertThat(result).contains("*"); // 密码应该包含星号脱敏
    }

    @Test
    @DisplayName("应该脱敏不同长度的密码")
    void shouldDesensitizeDifferentLengthPasswords() {
      // Arrange
      String shortPwd = "123456";
      String longPwd = "VeryLongPassword123!@#";
      Function<String, String> desensitizer = SensitiveStrategy.PASSWORD.desensitizer();

      // Act
      String result1 = desensitizer.apply(shortPwd);
      String result2 = desensitizer.apply(longPwd);

      // Assert
      assertThat(result1).doesNotContain("123456");
      assertThat(result2).doesNotContain("VeryLongPassword");
    }
  }

  @Nested
  @DisplayName("10. IPV4 (IP地址) 脱敏测试")
  class Ipv4Tests {

    @Test
    @DisplayName("应该脱敏IPv4地址")
    void shouldDesensitizeIpv4() {
      // Arrange
      String ip = "192.168.1.100";
      Function<String, String> desensitizer = SensitiveStrategy.IPV4.desensitizer();

      // Act
      String result = desensitizer.apply(ip);

      // Assert
      assertThat(result).isNotEqualTo(ip);
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理公网IP")
    void shouldHandlePublicIp() {
      // Arrange
      String publicIp = "8.8.8.8";
      Function<String, String> desensitizer = SensitiveStrategy.IPV4.desensitizer();

      // Act
      String result = desensitizer.apply(publicIp);

      // Assert
      assertThat(result).contains("*");
    }
  }

  @Nested
  @DisplayName("11. IPV6 (IPv6地址) 脱敏测试")
  class Ipv6Tests {

    @Test
    @DisplayName("应该脱敏IPv6地址")
    void shouldDesensitizeIpv6() {
      // Arrange
      String ipv6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
      Function<String, String> desensitizer = SensitiveStrategy.IPV6.desensitizer();

      // Act
      String result = desensitizer.apply(ipv6);

      // Assert
      assertThat(result).isNotEqualTo(ipv6);
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理简写IPv6")
    void shouldHandleCompressedIpv6() {
      // Arrange
      String ipv6 = "fe80::1";
      Function<String, String> desensitizer = SensitiveStrategy.IPV6.desensitizer();

      // Act
      String result = desensitizer.apply(ipv6);

      // Assert
      assertThat(result).contains("*");
    }
  }

  @Nested
  @DisplayName("12. CAR_LICENSE (车牌) 脱敏测试")
  class CarLicenseTests {

    @Test
    @DisplayName("应该脱敏普通车牌号")
    void shouldDesensitizeNormalCarLicense() {
      // Arrange
      String license = "京A12345";
      Function<String, String> desensitizer = SensitiveStrategy.CAR_LICENSE.desensitizer();

      // Act
      String result = desensitizer.apply(license);

      // Assert
      assertThat(result).isNotEqualTo(license);
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该脱敏新能源车牌号")
    void shouldDesensitizeNewEnergyCarLicense() {
      // Arrange
      String license = "京AD12345"; // 新能源车牌（D或F开头）
      Function<String, String> desensitizer = SensitiveStrategy.CAR_LICENSE.desensitizer();

      // Act
      String result = desensitizer.apply(license);

      // Assert
      assertThat(result).contains("*");
    }
  }

  @Nested
  @DisplayName("13. FIRST_MASK (首字符保留) 脱敏测试")
  class FirstMaskTests {

    @Test
    @DisplayName("应该只保留第一个字符")
    void shouldKeepOnlyFirstCharacter() {
      // Arrange
      String text = "重要信息";
      Function<String, String> desensitizer = SensitiveStrategy.FIRST_MASK.desensitizer();

      // Act
      String result = desensitizer.apply(text);

      // Assert
      assertThat(result).isNotEqualTo(text);
      assertThat(result).startsWith("重");
      assertThat(result).contains("*");
    }

    @Test
    @DisplayName("应该处理单个字符")
    void shouldHandleSingleCharacter() {
      // Arrange
      String singleChar = "A";
      Function<String, String> desensitizer = SensitiveStrategy.FIRST_MASK.desensitizer();

      // Act
      String result = desensitizer.apply(singleChar);

      // Assert
      assertThat(result).isEqualTo("A"); // 单个字符应该保持不变
    }
  }

  @Nested
  @DisplayName("14. CLEAR (清空为空字符串) 脱敏测试")
  class ClearTests {

    @Test
    @DisplayName("应该清空为空字符串")
    void shouldClearToEmptyString() {
      // Arrange
      String data = "敏感数据";
      Function<String, String> desensitizer = SensitiveStrategy.CLEAR.desensitizer();

      // Act
      String result = desensitizer.apply(data);

      // Assert
      assertThat(result).isEmpty();
      assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("应该清空任何内容")
    void shouldClearAnyContent() {
      // Arrange
      Function<String, String> desensitizer = SensitiveStrategy.CLEAR.desensitizer();

      // Act
      String result1 = desensitizer.apply("任何内容");
      String result2 = desensitizer.apply("123456");
      String result3 = desensitizer.apply("abc@example.com");

      // Assert
      assertThat(result1).isEmpty();
      assertThat(result2).isEmpty();
      assertThat(result3).isEmpty();
    }
  }

  @Nested
  @DisplayName("15. CLEAR_TO_NULL (清空为null) 脱敏测试")
  class ClearToNullTests {

    @Test
    @DisplayName("应该清空为null")
    void shouldClearToNull() {
      // Arrange
      String data = "敏感数据";
      Function<String, String> desensitizer = SensitiveStrategy.CLEAR_TO_NULL.desensitizer();

      // Act
      String result = desensitizer.apply(data);

      // Assert
      assertThat(result).isNull();
    }

    @Test
    @DisplayName("应该将任何内容清空为null")
    void shouldClearAnyContentToNull() {
      // Arrange
      Function<String, String> desensitizer = SensitiveStrategy.CLEAR_TO_NULL.desensitizer();

      // Act
      String result1 = desensitizer.apply("密码");
      String result2 = desensitizer.apply("13800138000");
      String result3 = desensitizer.apply("test@example.com");

      // Assert
      assertThat(result1).isNull();
      assertThat(result2).isNull();
      assertThat(result3).isNull();
    }
  }

  @Nested
  @DisplayName("16. 真实业务场景测试")
  class RealBusinessScenarioTests {

    @Test
    @DisplayName("场景: 用户信息导出时脱敏个人信息")
    void shouldDesensitizeUserPersonalInfo() {
      // Arrange - 模拟用户信息
      String name = "张三";
      String phone = "13800138000";
      String idCard = "110101199003071234";
      String email = "zhangsan@example.com";

      // Act
      String desensitizedName = SensitiveStrategy.CHINESE_NAME.desensitizer().apply(name);
      String desensitizedPhone = SensitiveStrategy.PHONE.desensitizer().apply(phone);
      String desensitizedIdCard = SensitiveStrategy.ID_CARD.desensitizer().apply(idCard);
      String desensitizedEmail = SensitiveStrategy.EMAIL.desensitizer().apply(email);

      // Assert
      assertThat(desensitizedName).isNotEqualTo(name).contains("*");
      assertThat(desensitizedPhone).isNotEqualTo(phone).contains("****");
      assertThat(desensitizedIdCard).isNotEqualTo(idCard).startsWith("110").endsWith("1234");
      assertThat(desensitizedEmail).isNotEqualTo(email).contains("@");
    }

    @Test
    @DisplayName("场景: 日志记录时脱敏用户密码")
    void shouldDesensitizePasswordInLogs() {
      // Arrange
      String password = "MySecretPassword123!";

      // Act
      String desensitized = SensitiveStrategy.PASSWORD.desensitizer().apply(password);

      // Assert
      assertThat(desensitized).doesNotContain("MySecretPassword");
      assertThat(desensitized).doesNotContain("123!");
    }

    @Test
    @DisplayName("场景: 订单信息展示时脱敏收货地址")
    void shouldDesensitizeDeliveryAddress() {
      // Arrange
      String address = "北京市朝阳区建国路88号SOHO现代城2号楼1501室";

      // Act
      String desensitized = SensitiveStrategy.ADDRESS.desensitizer().apply(address);

      // Assert
      assertThat(desensitized).isNotEqualTo(address);
      assertThat(desensitized).startsWith("北京市朝阳区");
    }

    @Test
    @DisplayName("场景: 支付信息脱敏银行卡号")
    void shouldDesensitizeBankCardInPayment() {
      // Arrange
      String bankCard = "6217000010012345678";

      // Act
      String desensitized = SensitiveStrategy.BANK_CARD.desensitizer().apply(bankCard);

      // Assert
      assertThat(desensitized).isNotEqualTo(bankCard);
      assertThat(desensitized).contains("*");
      // 银行卡号已脱敏，包含星号遮蔽敏感部分
    }

    @Test
    @DisplayName("场景: 系统日志记录用户IP时脱敏")
    void shouldDesensitizeIpInSystemLogs() {
      // Arrange
      String ipv4 = "192.168.1.100";
      String ipv6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";

      // Act
      String desensitizedIpv4 = SensitiveStrategy.IPV4.desensitizer().apply(ipv4);
      String desensitizedIpv6 = SensitiveStrategy.IPV6.desensitizer().apply(ipv6);

      // Assert
      assertThat(desensitizedIpv4).contains("*");
      assertThat(desensitizedIpv6).contains("*");
    }

    @Test
    @DisplayName("场景: 完全清除敏感数据")
    void shouldCompletelyClearSensitiveData() {
      // Arrange
      String sensitiveData = "极度敏感的商业机密";

      // Act
      String clearedToEmpty = SensitiveStrategy.CLEAR.desensitizer().apply(sensitiveData);
      String clearedToNull = SensitiveStrategy.CLEAR_TO_NULL.desensitizer().apply(sensitiveData);

      // Assert
      assertThat(clearedToEmpty).isEmpty();
      assertThat(clearedToNull).isNull();
    }
  }

  @Nested
  @DisplayName("17. 枚举基本属性测试")
  class EnumBasicTests {

    @Test
    @DisplayName("应该有15个脱敏策略")
    void shouldHave15Strategies() {
      // Act
      SensitiveStrategy[] strategies = SensitiveStrategy.values();

      // Assert
      assertThat(strategies).hasSize(15);
    }

    @Test
    @DisplayName("所有策略都应该有desensitizer函数")
    void allStrategiesShouldHaveDesensitizer() {
      // Arrange
      SensitiveStrategy[] strategies = SensitiveStrategy.values();

      // Act & Assert
      for (SensitiveStrategy strategy : strategies) {
        assertThat(strategy.desensitizer()).isNotNull();
      }
    }

    @Test
    @DisplayName("应该能够通过名称获取策略")
    void shouldGetStrategyByName() {
      // Act
      SensitiveStrategy strategy = SensitiveStrategy.valueOf("PHONE");

      // Assert
      assertThat(strategy).isEqualTo(SensitiveStrategy.PHONE);
    }

    @Test
    @DisplayName("验证所有策略枚举名称")
    void shouldVerifyAllStrategyNames() {
      // Act
      SensitiveStrategy[] strategies = SensitiveStrategy.values();

      // Assert
      assertThat(strategies)
          .extracting(Enum::name)
          .containsExactlyInAnyOrder(
              "ID_CARD",
              "PHONE",
              "ADDRESS",
              "EMAIL",
              "BANK_CARD",
              "CHINESE_NAME",
              "FIXED_PHONE",
              "USER_ID",
              "PASSWORD",
              "IPV4",
              "IPV6",
              "CAR_LICENSE",
              "FIRST_MASK",
              "CLEAR",
              "CLEAR_TO_NULL");
    }
  }
}
