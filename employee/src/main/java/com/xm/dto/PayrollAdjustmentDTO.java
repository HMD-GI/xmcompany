package com.xm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 薪资调整DTO
 */
@Data
public class PayrollAdjustmentDTO {
    
    private int payrollId; // 薪资单ID
    
    private BigDecimal performance; // 绩效工资调整
    
    private BigDecimal overtime; // 加班费调整
    
    private BigDecimal bonus; // 奖金调整
    
    private BigDecimal deduction; // 扣款调整
    
    private String remark; // 调整说明
} 