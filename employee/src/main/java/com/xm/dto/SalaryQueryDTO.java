package com.xm.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 薪资配置查询DTO
 */
@Data
public class SalaryQueryDTO {
    private Integer employeeId; // 员工ID
    private String employeeName; // 员工姓名
    private String bankCardNo; // 银行卡号
    private BigDecimal basicSalary; // 基本工资
    private Integer status; // 状态
}