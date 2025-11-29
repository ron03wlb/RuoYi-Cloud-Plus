package org.dromara.common.core.validate.dicts;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典项校验注解.
 *
 * @author AprilWind
 */
@Constraint(validatedBy = DictPatternValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface DictPattern {

  /** 字典类型，如 "sys_user_sex". */
  String dictType();

  /** 分隔符. */
  String separator();

  /** 默认校验失败提示信息. */
  String message() default "字典值无效";

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
