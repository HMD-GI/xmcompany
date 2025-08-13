package com.xm.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购订单VO
 */
@Data
public class SimplePurchaseOrderVO {
    private int id; // 采购订单ID
    private String orderNo; // 订单编号

//    private int supplierId; // 供应商ID

    private String supplierName; // 供应商名称

//    private String supplierCode; // 供应商编号
    private String itemName; // 物料名称
    private String unit; // 单位（个、件、kg等）

//    private BigDecimal unitPrice; // 单价
//    private int quantity; // 采购数量
//    private BigDecimal totalAmount; // 总金额
//    private int status; // 采购订单状态

    private String statusName; // 状态名称

//    private int purchaseRequestId; // 关联的采购申请ID
//    private String applicant; // 申请人
//    private String remark; // 备注
//    private LocalDateTime createTime; // 创建时间
//    private LocalDateTime updateTime; // 更新时间
} 