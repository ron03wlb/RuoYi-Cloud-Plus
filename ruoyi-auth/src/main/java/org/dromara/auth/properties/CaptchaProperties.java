package org.dromara.auth.properties;

import lombok.Data;
import org.dromara.auth.enums.CaptchaCategory;
import org.dromara.auth.enums.CaptchaType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * Captcha configuration properties for verification code generation and validation.
 *
 * @author ruoyi
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "security.captcha")
public class CaptchaProperties {

  /** 验证码类型 */
  private CaptchaType type;

  /** 验证码类别 */
  private CaptchaCategory category;

  /** 数字验证码位数 */
  private Integer numberLength;

  /** 字符验证码长度 */
  private Integer charLength;

  /** 验证码开关 */
  private Boolean enabled;
}
