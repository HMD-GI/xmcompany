package com.xm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 工资单查询DTO
 */
@Data
public class PayrollQueryDTO {
    private Integer employeeId; // 员工ID
    private String employeeName; // 员工姓名
    private String payrollMonth; // 薪资月份
    private String bankCardNo; // 银行卡号
    private BigDecimal minAmount; // 最小实发金额
    private BigDecimal maxAmount; // 最大实发金额
    private Integer status; // 状态
}