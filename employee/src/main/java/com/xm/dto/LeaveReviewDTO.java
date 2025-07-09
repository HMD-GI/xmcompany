package com.xm.dto;

import lombok.Data;

/**
 * 请假审核数据传输对象
 */
@Data
public class LeaveReviewDTO {
    
    private int leaveId; // 请假记录ID
    
    private int status; // 审核状态（1:批准, 2:拒绝）
    
    private String reviewerName; // 审核人姓名
    
    private String reviewComment; // 审核意见
} 