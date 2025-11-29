package org.dromara.common.core.exception.base;

import java.io.Serial;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.StringUtils;

/**
 * 基础异常.
 *
 * @author ruoyi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class BaseException extends RuntimeException {
  @Serial private static final long serialVersionUID = 1L;

  /** 所属模块. */
  private String module;

  /** 错误码. */
  private String code;

  /** 错误码对应的参数. */
  private Object[] args;

  /** 错误消息. */
  private String defaultMessage;

  /**
   * Constructs a BaseException with module, code and arguments.
   *
   * @param module the module name
   * @param code the error code
   * @param args the error code parameters
   */
  public BaseException(String module, String code, Object[] args) {
    this(module, code, args, null);
  }

  /**
   * Constructs a BaseException with module and default message.
   *
   * @param module the module name
   * @param defaultMessage the default error message
   */
  public BaseException(String module, String defaultMessage) {
    this(module, null, null, defaultMessage);
  }

  /**
   * Constructs a BaseException with code and arguments.
   *
   * @param code the error code
   * @param args the error code parameters
   */
  public BaseException(String code, Object[] args) {
    this(null, code, args, null);
  }

  /**
   * Constructs a BaseException with default message.
   *
   * @param defaultMessage the default error message
   */
  public BaseException(String defaultMessage) {
    this(null, null, null, defaultMessage);
  }

  @Override
  public String getMessage() {
    String message = null;
    if (!StringUtils.isEmpty(code)) {
      message = MessageUtils.message(code, args);
    }
    if (message == null) {
      message = defaultMessage;
    }
    return message;
  }
}
