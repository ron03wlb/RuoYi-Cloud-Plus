package org.dromara.common.tenant.properties;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 租户 配置属性.
 *
 * @author Lion Li
 */
@Data
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

  /** 是否启用. */
  private Boolean enable;

  /** 排除表. */
  private List<String> excludes;
}
