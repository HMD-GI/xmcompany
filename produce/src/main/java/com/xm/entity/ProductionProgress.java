package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生产进度记录实体类
 */
@Data
@TableName("production_progress")
public class ProductionProgress implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 记录ID，使用外部生成
    
    @TableField("project_id")
    private int projectId; // 项目ID
    
    @TableField("quantity")
    private int quantity; // 生产数量
    
    @TableField("before_quantity")
    private int beforeQuantity; // 更新前数量
    
    @TableField("after_quantity")
    private int afterQuantity; // 更新后数量
    
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