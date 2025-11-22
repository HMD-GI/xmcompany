package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 员工薪资单实体类
 */
@Data
@TableName("employee_payroll")
public class Payroll implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private int id; // 薪资单ID，使用Redis生成
    
    private int employeeId; // 员工ID
    
    private String employeeName; // 员工姓名
    
    private String bankCardNo; // 银行卡号
    
    private String bankName; // 开户银行
    
    private String payrollMonth; // 薪资月份（格式：yyyy-MM）/
    
    private BigDecimal basicSalary; // 基本工资
    
    private BigDecimal performance; // 绩效工资
    
    private BigDecimal allowance; // 补贴
    
    private BigDecimal overtime; // 加班费
    
    private BigDecimal bonus; // 奖金
    
    private BigDecimal deduction; // 扣款
    
    private BigDecimal insuranceAmount; // 五险一金
    
    private BigDecimal taxableAmount; // 应纳税额
    
    private BigDecimal tax; // 个人所得税
    
    private BigDecimal actualAmount; // 实发金额
    
    private int status; // 状态（0:待发放, 1:已发放, 2:已撤销）
    
    private String remark; // 备注
    
    private LocalDateTime payTime; // 发放时间
    
    private LocalDateTime createTime; // 创建时间
    
    private LocalDateTime updateTime; // 更新时间
} 