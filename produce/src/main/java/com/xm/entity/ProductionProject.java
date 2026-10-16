package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 生产项目实体类
 */
@Data
@TableName("production_project")
public class ProductionProject implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 项目ID，使用外部生成
    
    @TableField("project_name")
    private String projectName; // 项目名称
    
    @TableField("status")
    private String status; // 项目状态（进行中、暂停、已完成、已取消）
    
    @TableField("current_quantity")
    private int currentQuantity; // 当前生产数量
    
    @TableField("target_quantity")
    private int targetQuantity; // 目标生产数量
    
    @TableField("product_name")
    private String productName; // 产品名称
    
    @TableField("unit")
    private String unit; // 单位（个、件、kg等）
    
    @TableField("start_time")
    private LocalDateTime startTime; // 开始时间
    
    @TableField("expected_end_time")
    private LocalDateTime expectedEndTime; // 预计结束时间
    
    @TableField("actual_end_time")
    private LocalDateTime actualEndTime; // 实际结束时间
    
    @TableField("remark")
    private String remark; // 备注
    
    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间
    
    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间
}