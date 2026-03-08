package com.xm.dto;

import lombok.Data;

/**
 * 请假查询 DTO
 */
@Data
public class LeaveQueryDTO {
    private Integer employeeId; // 员工 ID（可选）
    private String employeeName; // 员工姓名（可选）
    private Integer status; // 状态（可选）
}
