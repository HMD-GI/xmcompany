package com.xm.entity;

import lombok.Data;

@Data
public class employeeLogin {
    private String username;      // 登录用户名
    private String password;      // 登录密码（建议加密存储）
    private String role;          // 用户角色（如：ADMIN, USER 等）
    private int enabled;      // 是否启用账户（默认启用，启用=1，禁用=0）
    private long lastLoginTime;   // 最近一次登录时间（毫秒）
}
