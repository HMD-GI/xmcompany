package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购订单实体类
 */
@Data
@TableName("purchase_order")
public class PurchaseOrder implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 采购订单ID，使用外部生成

    @TableField("order_no")
    private String orderNo; // 订单编号，业务编号，如：PO12345

    @TableField("supplier_id")
    private int supplierId; // 供应商ID

    @TableField("item_name")
    private String itemName; // 物料名称

    @TableField("unit_price")
    private BigDecimal unitPrice; // 单价

    @TableField("quantity")
    private int quantity; // 采购数量

    @TableField("total_amount")
    private BigDecimal totalAmount; // 总金额

    @TableField("status")
    private int status; // 采购订单状态（0:待确认, 1:已确认, 2:已下单, 3:已到货, 4:已完成, 5:已作废）

    @TableField("purchase_request_id")
    private int purchaseRequestId; // 关联的采购申请ID

    @TableField("remark")
    private String remark; // 备注

    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间

    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间
} 