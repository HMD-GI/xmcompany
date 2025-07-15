package com.xm.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 采购申请VO
 */
@Data
public class PurchaseRequestVO {
    private int id; // 采购申请ID
    private String applicant; // 申请人
    private String itemName; // 物料名称
    private int quantity; // 采购数量
    private double budget; // 预算金额
    private int status; // 采购申请状态
    private String statusName; // 状态名称
    private String remark; // 备注
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
} 