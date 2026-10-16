package com.xm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存视图对象
 */
@Data
public class StockVO {
    private int id; // 库存记录ID
    // 物料唯一性只由 materialName 保证
    private String materialName; // 物料名称（唯一）
    private String unit; // 单位
    // 已删除 warehouseLocation 字段，仓库信息如需扩展可单独设计仓库表
    private int quantity; // 库存数量
    private LocalDateTime lastStockInTime; // 最后入库时间
    private LocalDateTime lastStockOutTime; // 最后出库时间
    private LocalDateTime createTime; // 创建时间
    private LocalDateTime updateTime; // 更新时间
} 