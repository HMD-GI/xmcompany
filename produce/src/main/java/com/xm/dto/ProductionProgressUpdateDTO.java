package com.xm.dto;

import lombok.Data;

/**
 * 更新生产进度DTO
 */
@Data
public class ProductionProgressUpdateDTO {
    private int projectId; // 项目ID
    private int quantity; // 新增生产数量
    private String remark; // 备注
}