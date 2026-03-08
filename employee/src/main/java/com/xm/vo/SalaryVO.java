package com.xm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工薪资配置VO
 */
@Data
public class SalaryVO implements Serializable {
    
    private int id; // 薪资配置ID
    
    private int employeeId; // 员工ID
    
    private String employeeName; // 员工姓名
    
    private String bankCardNo; // 银行卡号（隐藏部分数字）
    
    private String bankName; // 开户银行
    
    private BigDecimal basicSalary; // 基本工资
    
    private BigDecimal performanceBase; // 绩效基数
    
    private BigDecimal allowance; // 固定补贴
    
    private BigDecimal insuranceAmount; // 五险一金金额
    
    private BigDecimal totalBase; // 薪资基数合计
    
    private String statusDesc; // 状态描述
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime effectiveDate; // 生效日期
    
    private String remark; // 备注
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime; // 创建时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime; // 更新时间
} 