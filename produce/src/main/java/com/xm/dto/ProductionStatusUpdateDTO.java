package com.xm.dto;

import lombok.Data;

/**
 * 更新生产项目状态DTO
 */
@Data
public class ProductionStatusUpdateDTO {
    private int projectId; // 项目ID
    private String status; // 新状态
    private String remark; // 备注
}