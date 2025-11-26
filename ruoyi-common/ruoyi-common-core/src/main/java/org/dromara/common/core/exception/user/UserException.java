package org.dromara.common.core.exception.user;

import java.io.Serial;
import org.dromara.common.core.exception.base.BaseException;

/**
 * 用户信息异常类
 *
 * @author ruoyi
 */
public class UserException extends BaseException {

    @Serial private static final long serialVersionUID = 1L;

    public UserException(String code, Object... args) {
        super("user", code, args, null);
    }
}
