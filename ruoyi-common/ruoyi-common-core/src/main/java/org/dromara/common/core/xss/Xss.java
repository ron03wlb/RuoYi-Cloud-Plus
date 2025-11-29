package org.dromara.common.core.xss;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义xss校验注解.
 *
 * @author Lion Li
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
    value = {ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Constraint(validatedBy = {XssValidator.class})
public @interface Xss {

  /**
   * Validation error message.
   *
   * @return the error message template
   */
  String message() default "不允许任何脚本运行";

  /**
   * Validation groups for this constraint.
   *
   * @return the groups the constraint belongs to
   */
  Class<?>[] groups() default {};

  /**
   * Payload type that can be attached to this constraint.
   *
   * @return the payload types
   */
  Class<? extends Payload>[] payload() default {};
}
