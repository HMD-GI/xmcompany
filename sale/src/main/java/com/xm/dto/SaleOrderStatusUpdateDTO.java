package com.xm.dto;

import lombok.Data;

/**
 * 销售订单状态更新DTO
 */
@Data
public class SaleOrderStatusUpdateDTO {
    private int id; // 订单ID
    private int status; // 新状态
    private String remark; // 备注
}