package org.dromara.common.encrypt.annotation;

import java.lang.annotation.*;

/**
 * API encryption annotation for enforcing encryption on HTTP endpoints.
 *
 * <p>This annotation can be applied to controller methods to enforce encryption on HTTP responses.
 * By default, responses are not encrypted unless explicitly enabled.
 *
 * @author Michelle.Chung
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiEncrypt {

  /**
   * Indicates whether response encryption should be enabled.
   *
   * @return {@code true} to enable response encryption, {@code false} otherwise (default: false)
   */
  boolean response() default false;
}
