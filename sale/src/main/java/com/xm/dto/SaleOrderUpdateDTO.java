package com.xm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 销售订单更新DTO
 */
@Data
public class SaleOrderUpdateDTO {
    private int id; // 订单ID
    private String productName; // 产品名称
    private int quantity; // 数量
    private String unit; // 单位（个、件、kg等）
    private BigDecimal amount; // 订单金额
    private LocalDateTime deliveryTime; // 交货时间
    private String remark; // 备注
}