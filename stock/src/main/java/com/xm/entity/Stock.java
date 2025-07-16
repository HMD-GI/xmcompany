package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存实体类
 */
@Data
@TableName("stock")
public class Stock implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 库存记录ID，使用外部生成
    
    // 物料唯一性只由 materialName 保证
    @TableField("material_name")
    private String materialName; // 物料名称（唯一）
    
    @TableField("unit")
    private String unit; // 单位（个、件、kg等）
    
    // 已删除 warehouseLocation 字段，仓库信息如需扩展可单独设计仓库表
    
    @TableField("quantity")
    private int quantity; // 库存数量
    
    @TableField("last_stock_in_time")
    private LocalDateTime lastStockInTime; // 最后入库时间
    
    @TableField("last_stock_out_time")
    private LocalDateTime lastStockOutTime; // 最后出库时间
    
    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间
    
    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间
} 