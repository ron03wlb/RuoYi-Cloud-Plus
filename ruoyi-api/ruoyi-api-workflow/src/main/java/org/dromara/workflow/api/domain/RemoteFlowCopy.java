package org.dromara.workflow.api.domain;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 抄送.
 *
 * @author may
 */
@Data
public class RemoteFlowCopy implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  /** 用户id. */
  private Long userId;

  /** 用户名称. */
  private String userName;
}
