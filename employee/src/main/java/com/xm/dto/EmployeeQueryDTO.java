package com.xm.dto;

import lombok.Data;

/**
 * 员工查询DTO
 */
@Data
public class EmployeeQueryDTO {
    private String name; // 员工姓名
    private String gender; // 性别
    private String role; // 用户角色
    private Integer enabled; // 是否启用账户（1 启用，0 禁用）
}