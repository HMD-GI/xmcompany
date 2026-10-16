package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工请假实体类
 */
@Data
@TableName("employee_leave")
public class Leave implements Serializable {
    
    @TableId(type = IdType.INPUT)
    private int id; // 请假记录ID，使用Redis生成
    
    private int employeeId; // 员工ID
    
    private String employeeName; // 员工姓名
    
    private String leaveType; // 请假类型（1:事假, 2:病假, 3:年假, 4:调休, 5:婚假, 6:产假, 7:丧假）
    
    private LocalDateTime startTime; // 请假开始时间
    
    private LocalDateTime endTime; // 请假结束时间
    
    private String reason; // 请假原因
    
    private int status; // 请假状态（0:待审核, 1:已批准, 2:已拒绝, 3:已取消）
    
    private String reviewerName; // 审核人姓名
    
    private String reviewComment; // 审核意见
    
    private LocalDateTime reviewTime; // 审核时间
    
    private LocalDateTime createTime; // 创建时间
    
    private LocalDateTime updateTime; // 更新时间
} 