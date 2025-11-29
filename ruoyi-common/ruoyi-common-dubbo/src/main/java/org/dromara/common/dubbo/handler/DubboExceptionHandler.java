package org.dromara.common.dubbo.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Dubbo异常处理器.
 *
 * @author Lion Li
 */
@Slf4j
@RestControllerAdvice
public class DubboExceptionHandler {

  // 手动添加 log 字段（Lombok @Slf4j 未生效时的临时解决方案）
  private static final org.slf4j.Logger log =
      org.slf4j.LoggerFactory.getLogger(DubboExceptionHandler.class);

  /** 主键或UNIQUE索引，数据重复异常. */
  @ExceptionHandler(RpcException.class)
  public R<Void> handleDubboException(RpcException e) {
    log.error("RPC异常: {}", e.getMessage());
    return R.fail("RPC异常，请联系管理员确认");
  }
}
