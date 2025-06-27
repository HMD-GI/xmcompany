package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class employeeLogin {
    @TableId(type = IdType.AUTO)
    private int id; //账号表ID
    private int employeeId; //用户ID
    private String username;      // 登录用户名
    private String password;      // 登录密码（建议加密存储）
    private String role;          // 用户角色（如：ADMIN, USER 等）
    private int enabled;      // 是否启用账户（默认启用，启用=1，禁用=0）
    private LocalDateTime lastLoginTime;   // 最近一次登录时间
    private LocalDateTime createTime;   // 账户创建时间
}