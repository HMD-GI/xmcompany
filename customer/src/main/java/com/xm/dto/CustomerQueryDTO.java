package com.xm.dto;

import lombok.Data;

/**
 * 客户查询DTO
 */
@Data
public class CustomerQueryDTO {
    private String name; // 客户名称
    private String contactPerson; // 联系人
    private String source; // 客户来源
    private Integer level; // 客户等级
    private Integer status; // 客户状态（1: 潜在客户, 2: 意向客户, 3: VIP客户, 4: 已成交客户）

}
