package com.xm.dto;

import lombok.Data;

/**
 * 生产项目查询DTO
 */
@Data
public class ProductionProjectQueryDTO {
    private String projectName; // 项目名称
    private String status; // 项目状态
    private String productName; // 产品名称
}