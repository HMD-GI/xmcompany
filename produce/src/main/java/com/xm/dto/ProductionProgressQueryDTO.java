package com.xm.dto;

import lombok.Data;

/**
 * 生产进度查询DTO
 */
@Data
public class ProductionProgressQueryDTO {
    private Integer projectId; // 项目ID
    private String projectName; // 项目名称
    private String operatorName; // 操作人姓名
}