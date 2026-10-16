package com.xm.dto;

import lombok.Data;

/**
 * 工资单生成DTO
 */
@Data
public class PayrollGenerationDTO {
    
    private String month; // 薪资月份（格式：yyyy-MM）
    
    private Integer employeeId; // 员工ID，为空表示生成所有员工的工资单
} 