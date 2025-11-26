package org.dromara.common.core.domain;

import io.github.linpeilie.annotations.AutoMapper;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 测试用订单视图对象
 *
 * @author Test Team
 */
@AutoMapper(target = Order.class)
public class OrderVO implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;

    public OrderVO() {}

    public OrderVO(
            Long orderId, String orderNo, Long userId, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.orderNo = orderNo;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
