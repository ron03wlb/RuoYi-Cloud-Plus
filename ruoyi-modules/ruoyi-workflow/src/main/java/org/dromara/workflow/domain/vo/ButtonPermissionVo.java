package org.dromara.workflow.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import lombok.Data;

/**
 * 按钮权限
 *
 * @author may
 * @date 2025-02-28
 */
@Data
public class ButtonPermissionVo implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 唯一编码 */
    private String code;

    /** 选项值 */
    private String value;

    /** 是否显示 */
    private Boolean show;

    public ButtonPermissionVo() {}

    public ButtonPermissionVo(String code, Boolean show) {
        this.code = code;
        this.show = show;
    }
}
