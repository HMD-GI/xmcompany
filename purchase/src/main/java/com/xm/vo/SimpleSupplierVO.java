package com.xm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 供应商数据传输对象
 */
@Data
public class SimpleSupplierVO {
    
    private int id; // 供应商ID
    private String code; // 供应商编号
    private String name; // 供应商名称

//    private String contactPerson; // 联系人
//    private String phone; // 联系电话
//    private String email; // 邮箱
//    private String address; // 地址
    private String productRange; // 产品范围

    private int status; // 供应商状态

//    private String statusName; // 状态名称/

//    private String bankAccount; // 银行账号
//    private String bankName; // 开户银行
//    private LocalDateTime createTime; // 创建时间
//    private LocalDateTime updateTime; // 更新时间
//    private String remark; // 备注
} 