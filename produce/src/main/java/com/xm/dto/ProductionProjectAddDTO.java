package com.xm.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 新增生产项目DTO
 */
@Data
public class ProductionProjectAddDTO {
    private String projectName; // 项目名称
    private int targetQuantity; // 目标生产数量
    private String productName; // 产品名称
    private String unit; // 单位（个、件、kg等）
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime expectedEndTime; // 预计结束时间
    private String remark; // 备注
}