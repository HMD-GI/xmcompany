package com.xm.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 薪资单VO
 */
@Data
public class PayrollVO implements Serializable {
    
    private int id; // 薪资单ID
    
    private int employeeId; // 员工ID
    
    private String employeeName; // 员工姓名
    
    private String bankCardNo; // 银行卡号（隐藏部分数字）
    
    private String bankName; // 开户银行
    
    private String payrollMonth; // 薪资月份（格式：yyyy-MM）
    
    private BigDecimal basicSalary; // 基本工资
    
    private BigDecimal performance; // 绩效工资
    
    private BigDecimal allowance; // 补贴
    
    private BigDecimal overtime; // 加班费
    
    private BigDecimal bonus; // 奖金
    
    private BigDecimal totalIncome; // 收入合计
    
    private BigDecimal deduction; // 扣款
    
    private BigDecimal insuranceAmount; // 五险一金
    
    private BigDecimal taxableAmount; // 应纳税额
    
    private BigDecimal tax; // 个人所得税
    
    private BigDecimal totalDeduction; // 扣除合计
    
    private BigDecimal actualAmount; // 实发金额
    
    private int status; // 状态
    
    private String statusDesc; // 状态描述
    
    private String remark; // 备注
    
    private LocalDateTime payTime; // 发放时间
    
    private LocalDateTime createTime; // 创建时间
} 