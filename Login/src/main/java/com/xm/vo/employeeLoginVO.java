package com.xm.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class employeeLoginVO implements Serializable {
    private int employeeId; //用户ID
    private String name;         // 员工姓名
    private String role;          // 用户角色（如：ADMIN, USER 等）
    private int enabled;      // 是否启用账户（默认启用，启用=1，禁用=0）

    private String token;  //返回jwt令牌
}