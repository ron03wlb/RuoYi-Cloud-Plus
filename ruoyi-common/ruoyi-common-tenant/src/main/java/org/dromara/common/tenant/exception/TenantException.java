package org.dromara.common.tenant.exception;

import java.io.Serial;
import org.dromara.common.core.exception.base.BaseException;

/**
 * 租户异常类
 *
 * @author Lion Li
 */
public class TenantException extends BaseException {

    @Serial private static final long serialVersionUID = 1L;

    public TenantException(String code, Object... args) {
        super("tenant", code, args, null);
    }
}
