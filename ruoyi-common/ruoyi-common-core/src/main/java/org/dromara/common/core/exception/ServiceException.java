package org.dromara.common.core.exception;

import cn.hutool.core.text.StrFormatter;
import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务异常（支持占位符 {} ）.
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public final class ServiceException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  /** 错误码. */
  private Integer code;

  /** 错误提示. */
  private String message;

  /** 错误明细，内部调试错误. */
  private String detailMessage;

  /**
   * Constructs a ServiceException with error message.
   *
   * @param message the error message
   */
  public ServiceException(String message) {
    this.message = message;
  }

  /**
   * Constructs a ServiceException with error message and code.
   *
   * @param message the error message
   * @param code the error code
   */
  public ServiceException(String message, Integer code) {
    this.message = message;
    this.code = code;
  }

  /**
   * Constructs a ServiceException with formatted message using placeholders.
   *
   * @param message the error message template with {} placeholders
   * @param args the arguments to fill placeholders
   */
  public ServiceException(String message, Object... args) {
    this.message = StrFormatter.format(message, args);
  }

  /**
   * Gets the exception message.
   *
   * @return the error message
   */
  @Override
  public String getMessage() {
    return message;
  }

  /**
   * Sets the exception message.
   *
   * @param message the error message
   * @return this ServiceException instance
   */
  public ServiceException setMessage(String message) {
    this.message = message;
    return this;
  }

  /**
   * Sets the detailed exception message.
   *
   * @param detailMessage the detailed error message for debugging
   * @return this ServiceException instance
   */
  public ServiceException setDetailMessage(String detailMessage) {
    this.detailMessage = detailMessage;
    return this;
  }
}
