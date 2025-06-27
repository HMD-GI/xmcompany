package com.xm.vo;

import lombok.Data;

@Data
public class employeeLoginVO {
    private int employeeId; //用户ID
    private String role;          // 用户角色（如：ADMIN, USER 等）
    private int enabled;      // 是否启用账户（默认启用，启用=1，禁用=0）

    private String token;  //返回jwt令牌
}