package org.dromara.common.core.exception.file;

import java.io.Serial;
import org.dromara.common.core.exception.base.BaseException;

/**
 * 文件信息异常类.
 *
 * @author ruoyi
 */
public class FileException extends BaseException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a FileException with error code and arguments.
   *
   * @param code the error code
   * @param args the error code parameters
   */
  public FileException(String code, Object[] args) {
    super("file", code, args, null);
  }
}
