package com.xm.dto;

import lombok.Data;

/**
 * 出库DTO
 */
@Data
public class StockOutDTO {
    private int stockId; // 库存ID
    private int quantity; // 出库数量
    private String remark; // 备注
    private String materialName; // 物料名称（唯一）
    private String unit; // 单位（个、件、kg等）
} 