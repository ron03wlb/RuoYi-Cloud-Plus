package org.dromara.common.core.exception.user;

import java.io.Serial;

/**
 * 验证码错误异常类.
 *
 * @author Lion Li
 */
public class CaptchaException extends UserException {
  @Serial private static final long serialVersionUID = 1L;

  /** Constructs a CaptchaException with default error code. */
  public CaptchaException() {
    super("user.jcaptcha.error");
  }

  /**
   * Constructs a CaptchaException with custom error code.
   *
   * @param msg the error code
   */
  public CaptchaException(String msg) {
    super(msg);
  }
}
