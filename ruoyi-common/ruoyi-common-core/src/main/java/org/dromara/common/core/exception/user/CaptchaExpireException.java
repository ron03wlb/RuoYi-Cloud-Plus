package org.dromara.common.core.exception.user;

import java.io.Serial;

/**
 * 验证码失效异常类.
 *
 * @author ruoyi
 */
public class CaptchaExpireException extends UserException {

  @Serial private static final long serialVersionUID = 1L;

  /** Constructs a CaptchaExpireException with default error code. */
  public CaptchaExpireException() {
    super("user.jcaptcha.expire");
  }
}
