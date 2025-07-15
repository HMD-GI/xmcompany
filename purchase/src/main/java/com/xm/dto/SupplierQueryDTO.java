package com.xm.dto;

import lombok.Data;

/**
 * 供应商查询参数对象
 */
@Data
public class SupplierQueryDTO {
    
    private String code; // 供应商编号，支持模糊查询
    private String name; // 供应商名称，支持模糊查询
    private String contactPerson; // 联系人，支持模糊查询
    private String phone; // 联系电话，支持模糊查询
    private Integer status; // 供应商状态
    private String productRange; // 产品范围，支持模糊查询
} 