package com.xm.dto;

import lombok.Data;

/**
 * 入库DTO
 */
@Data
public class StockInDTO {
    private String materialName; // 物料名称（唯一）
    private String unit; // 单位（个、件、kg等）
    private int quantity; // 入库数量
    private String remark; // 备注
} 