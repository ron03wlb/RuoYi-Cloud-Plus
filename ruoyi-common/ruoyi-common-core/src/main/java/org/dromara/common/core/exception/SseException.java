package org.dromara.common.core.exception;

import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * sse 特制异常.
 *
 * @author LionLi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public final class SseException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  /** 错误码. */
  private Integer code;

  /** 错误提示. */
  private String message;

  /** 错误明细，内部调试错误. */
  private String detailMessage;

  /**
   * Constructs a SseException with error message.
   *
   * @param message the error message
   */
  public SseException(String message) {
    this.message = message;
  }

  /**
   * Constructs a SseException with error message and code.
   *
   * @param message the error message
   * @param code the error code
   */
  public SseException(String message, Integer code) {
    this.message = message;
    this.code = code;
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
   * @return this SseException instance
   */
  public SseException setMessage(String message) {
    this.message = message;
    return this;
  }

  /**
   * Sets the detailed exception message.
   *
   * @param detailMessage the detailed error message for debugging
   * @return this SseException instance
   */
  public SseException setDetailMessage(String detailMessage) {
    this.detailMessage = detailMessage;
    return this;
  }
}
