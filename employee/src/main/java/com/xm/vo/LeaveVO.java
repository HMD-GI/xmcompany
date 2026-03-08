package com.xm.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 请假信息VO对象
 */
@Data
public class LeaveVO implements Serializable {
    
    private int id; // 请假记录ID
    
    private int employeeId; // 员工ID
    
    private String employeeName; // 员工姓名
    
    private String leaveType; // 请假类型
    
    private LocalDateTime startTime; // 请假开始时间
    
    private LocalDateTime endTime; // 请假结束时间
//
//    private String reason; // 请假原因
//
    private int status; // 请假状态
//
//    private String statusDesc; // 请假状态描述
//
//    private String reviewerName; // 审核人姓名
//
//    private String reviewComment; // 审核意见
//
//    private LocalDateTime reviewTime; // 审核时间
//
//    private LocalDateTime createTime; // 创建时间
//
//    private LocalDateTime updateTime; // 更新时间
//
    private int days; // 请假天数
} 