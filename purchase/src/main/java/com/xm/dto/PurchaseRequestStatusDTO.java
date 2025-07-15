package com.xm.dto;

import lombok.Data;

/**
 * 采购申请状态变更DTO
 */
@Data
public class PurchaseRequestStatusDTO {
    private int id; // 采购申请ID
    private int status; // 新的采购申请状态
} 