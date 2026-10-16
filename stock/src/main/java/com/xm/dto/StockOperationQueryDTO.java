package com.xm.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存操作记录查询DTO
 */
@Data
public class StockOperationQueryDTO {
    private String operationNo; // 操作编号
    private String materialName; // 物料名称
    private String unit; // 单位（个、件、kg等）
    private Integer operationType; // 操作类型
    private Integer operatorId; // 操作人ID
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime; // 结束时间
} 