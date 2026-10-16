package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 供应商实体类
 */
@Data
@TableName("supplier")
public class Supplier implements Serializable {
    
    // 修改ID生成策略为INPUT，表示ID值由外部输入，不依赖数据库自增
    @TableId(type = IdType.INPUT)
    private int id; // 供应商ID，使用Redis生成
    
    private String code; // 供应商编号，业务编号，如：SP12345
    private String name; // 供应商名称
    private String contactPerson; // 联系人
    private String phone; // 联系电话
    private String email; // 邮箱
    private String address; // 地址
    
    @TableField("product_range")
    private String productRange; // 产品范围
    
    private int status; // 供应商状态（1: 合作中, 2: 已暂停, 3: 已终止）
    
    @TableField("bank_account")
    private String bankAccount; // 银行账号
    
    @TableField("bank_name")
    private String bankName; // 开户银行
    
    @TableField("create_time")
    private LocalDateTime createTime; // 创建时间
    
    @TableField("update_time")
    private LocalDateTime updateTime; // 更新时间
    
    private String remark; // 备注
} 