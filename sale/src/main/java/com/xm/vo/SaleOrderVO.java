package com.xm.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售订单VO
 */
@Data
public class SaleOrderVO {
    private int id; // 订单ID
    private String orderNo; // 订单编号

    private int customerId; // 客户ID
    private String customerName; // 客户名称
    private String productName; // 产品名称
    private int quantity; // 数量
    private String unit; // 单位（个、件、kg等）
    private BigDecimal amount; // 订单金额
    private int status; // 订单状态（0:待处理, 1:已确认, 2:已发货, 3:已完成, 4:已取消）
    private String statusName; // 订单状态名称
    private LocalDate deliveryTime; // 交货时间
    private LocalDateTime shippingTime; // 发货时间
    private String operatorName; // 操作人姓名
    private String remark; // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
}