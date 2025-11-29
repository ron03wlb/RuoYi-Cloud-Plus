package org.dromara.common.doc.config;

import io.swagger.v3.oas.models.Paths;

/**
 * 单独使用一个类便于判断 解决springdoc路径拼接重复问题.
 *
 * @author Lion Li
 */
public class PlusPaths extends Paths {

  /**
   * Constructs a new PlusPaths instance. This constructor is used to create a distinct Paths
   * subclass that can be identified to prevent duplicate path prefix processing in SpringDoc
   * configuration.
   */
  public PlusPaths() {
    super();
  }
}
