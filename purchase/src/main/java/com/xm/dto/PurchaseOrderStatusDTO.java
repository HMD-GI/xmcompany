package com.xm.dto;

import lombok.Data;

/**
 * 采购订单状态变更DTO
 */
@Data
public class PurchaseOrderStatusDTO {
    private int id; // 采购订单ID
    private int status; // 新的采购订单状态
} 