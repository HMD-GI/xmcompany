package com.xm.dto;

import lombok.Data;

/**
 * 供应商状态更新参数对象
 */
@Data
public class SupplierStatusDTO {
    
    private int id; // 供应商ID
    private int status; // 供应商状态（1: 合作中, 2: 已暂停, 3: 已终止）
    private String remark; // 状态变更原因，可选
} 