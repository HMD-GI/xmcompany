package com.xm.dto;

import lombok.Data;

/**
 * 采购申请查询参数对象
 */
@Data
public class PurchaseRequestQueryDTO {
    private String applicant; // 申请人，支持模糊查询
    private String itemName; // 物料名称，支持模糊查询
    private Integer status; // 采购申请状态
    private String unit; // 单位（个、件、kg等）
} 