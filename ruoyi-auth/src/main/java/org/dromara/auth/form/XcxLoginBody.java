package org.dromara.auth.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.domain.model.LoginBody;

/**
 * WeChat mini-program login request body.
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class XcxLoginBody extends LoginBody {

  /** 小程序id(多个小程序时使用). */
  private String appid;

  /** 小程序code. */
  @NotBlank(message = "{xcx.code.not.blank}")
  private String xcxCode;
}
