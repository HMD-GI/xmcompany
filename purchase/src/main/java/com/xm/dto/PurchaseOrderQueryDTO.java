package com.xm.dto;

import lombok.Data;

/**
 * 采购订单查询参数对象
 */
@Data
public class PurchaseOrderQueryDTO {
    private String orderNo; // 订单编号，支持模糊查询
    private Integer supplierId; // 供应商ID
    private String supplierName; // 供应商名称，支持模糊查询
    private String itemName; // 物料名称，支持模糊查询
    private Integer status; // 采购订单状态
    private Integer purchaseRequestId; // 采购申请ID
    private String unit; // 单位（个、件、kg等）
} 