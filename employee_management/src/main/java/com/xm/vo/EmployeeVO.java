package com.xm.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class EmployeeVO implements Serializable {
    private int id; // 员工ID
    private String name; // 员工姓名
    private String role; // 用户角色
    private int enabled; // 是否启用账户（1 启用，0 禁用）
    private LocalDateTime createTime; // 账户创建时间
    private String gender; // 性别
    private String phone; // 手机号
    private String email; // 邮箱号
}