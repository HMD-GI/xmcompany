package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 库存操作记录实体类
 */
@Data
@TableName("stock_operation")
public class StockOperation implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 操作记录ID，使用外部生成
    
    @TableField("operation_no")
    private String operationNo; // 操作编号，如：IN12345, OUT12345
    
    @TableField("stock_id")
    private int stockId; // 库存ID
    
    @TableField("operation_type")
    private int operationType; // 操作类型（0:入库, 1:出库, 2:库存调整）
    
    @TableField("quantity")
    private int quantity; // 操作数量
    
    @TableField("before_quantity")
    private int beforeQuantity; // 操作前库存数量
    
    @TableField("after_quantity")
    private int afterQuantity; // 操作后库存数量
    
    @TableField("operator_id")
    private int operatorId; // 操作人ID
    
    @TableField("operator_name")
    private String operatorName; // 操作人姓名
    
    @TableField("remark")
    private String remark; // 备注
    
    @TableField("operation_time")
    private LocalDateTime operationTime; // 操作时间
    
    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间
} 