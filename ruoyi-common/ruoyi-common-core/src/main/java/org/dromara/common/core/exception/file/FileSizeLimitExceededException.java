package org.dromara.common.core.exception.file;

import java.io.Serial;

/**
 * 文件名大小限制异常类.
 *
 * @author ruoyi
 */
public class FileSizeLimitExceededException extends FileException {

  @Serial private static final long serialVersionUID = 1L;

  /**
   * Constructs a FileSizeLimitExceededException with the maximum file size.
   *
   * @param defaultMaxSize the maximum allowed file size in bytes
   */
  public FileSizeLimitExceededException(long defaultMaxSize) {
    super("upload.exceed.maxSize", new Object[] {defaultMaxSize});
  }
}
