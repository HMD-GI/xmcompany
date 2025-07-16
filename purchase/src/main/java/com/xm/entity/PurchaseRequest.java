package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 采购申请实体类
 */
@Data
@TableName("purchase_request")
public class PurchaseRequest implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 采购申请ID，使用外部生成

    @TableField("applicant")
    private String applicant; // 申请人

    @TableField("item_name")
    private String itemName; // 物料名称
    
    @TableField("unit")
    private String unit; // 单位（个、件、kg等）

    @TableField("quantity")
    private int quantity; // 采购数量

    @TableField("budget")
    private double budget; // 预算金额

    @TableField("status")
    private int status; // 采购申请状态（0:草稿, 1:审批中, 2:已通过, 3:已驳回, 4:已撤回, 5:已完成）

    @TableField("remark")
    private String remark; // 备注

    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间

    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间
} 