package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工薪资配置实体类
 */
@Data
@TableName("employee_salary")
public class Salary implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private int id; // 薪资配置ID，使用Redis生成
    
    private int employeeId; // 员工ID
    
    private String employeeName; // 员工姓名
    
    private String bankCardNo; // 银行卡号
    
    private String bankName; // 开户银行
    
    private BigDecimal basicSalary; // 基本工资
    
    private BigDecimal performanceBase; // 绩效基数
    
    private BigDecimal allowance; // 固定补贴
    
    private BigDecimal insuranceAmount; // 五险一金金额
    
    private LocalDateTime effectiveDate; // 生效日期
    
    private int status; // 状态（1:有效, 0:无效）
    
    private String remark; // 备注
    
    private LocalDateTime createTime; // 创建时间
    
    private LocalDateTime updateTime; // 更新时间
} 