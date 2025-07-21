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
 * 销售订单实体类
 */
@Data
@TableName("sale_order")
public class SaleOrder implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 订单ID，使用外部生成
    
    @TableField("order_no")
    private String orderNo; // 订单编号，如：SO12345
    
    @TableField("customer_id")
    private int customerId; // 客户ID
    
    @TableField("customer_name")
    private String customerName; // 客户名称
    
    @TableField("product_name")
    private String productName; // 产品名称
    
    @TableField("quantity")
    private int quantity; // 数量
    
    @TableField("unit")
    private String unit; // 单位（个、件、kg等）
    
    @TableField("amount")
    private BigDecimal amount; // 订单金额
    
    @TableField("status")
    private int status; // 订单状态（0:待处理, 1:已确认, 2:已发货, 3:已完成, 4:已取消）
    
    @TableField("delivery_time")
    private LocalDateTime deliveryTime; // 交货时间
    
    @TableField("shipping_time")
    private LocalDateTime shippingTime; // 发货时间
    
    @TableField("operator_id")
    private int operatorId; // 操作人ID
    
    @TableField("operator_name")
    private String operatorName; // 操作人姓名
    
    @TableField("remark")
    private String remark; // 备注
    
    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间
    
    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间
}