package com.xm.dto;

import lombok.Data;

/**
 * 修改密码数据传输对象
 */

@Data
public class ChangePasswordDTO {
    private int id;
    private String newPassword;
    private String oldPassword;
}
