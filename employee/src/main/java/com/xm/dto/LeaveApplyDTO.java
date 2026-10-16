package com.xm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 请假申请数据传输对象
 */
@Data
public class LeaveApplyDTO {
    
    private int employeeId; // 员工ID
    
    private String leaveType; // 请假类型（1:事假, 2:病假, 3:年假, 4:调休, 5:婚假, 6:产假, 7:丧假）
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime; // 请假开始时间
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime; // 请假结束时间
    
    private String reason; // 请假原因
} 