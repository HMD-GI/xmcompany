package com.xm.dto;

import lombok.Data;

/**
 * 销售订单查询DTO
 */
@Data
public class SaleOrderQueryDTO {
    private String orderNo; // 订单编号
    private Integer customerId; // 客户ID
    private String customerName; // 客户名称
    private String productName; // 产品名称
    private Integer status; // 订单状态
}