package com.xm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工薪资配置DTO
 */
@Data
public class SalaryDTO {
    
    private int employeeId; // 员工ID
    
    private String bankCardNo; // 银行卡号
    
    private String bankName; // 开户银行
    
    private BigDecimal basicSalary; // 基本工资
    
    private BigDecimal performanceBase; // 绩效基数
    
    private BigDecimal allowance; // 固定补贴
    
    private BigDecimal insuranceAmount; // 五险一金金额
    
    private String remark; // 备注
} 