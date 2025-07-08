package com.xm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {
    @TableId(type = IdType.INPUT)
    private int id; // 员工ID，使用Redis生成
    private String name; // 员工姓名
    private String username; // 登录用户名
    private String password; // 登录密码
    private String role; // 用户角色，默认 USER
    private int enabled; // 是否启用账户（1 启用，0 禁用）
    private LocalDateTime createTime; // 账户创建时间
    private String gender; // 性别
    private String phone; // 手机号
    private String email; // 邮箱号
}