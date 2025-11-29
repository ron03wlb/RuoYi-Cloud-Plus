package org.dromara.auth.domain.vo;

import java.util.List;
import lombok.Data;

/**
 * Login tenant view object containing tenant selection information.
 *
 * @author Michelle.Chung
 */
@Data
public class LoginTenantVo {

  /** 租户开关 */
  private Boolean tenantEnabled;

  /** 租户对象列表 */
  private List<TenantListVo> voList;
}
