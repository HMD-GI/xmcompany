package com.xm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生产进度记录视图对象
 */
@Data
public class ProductionProgressVO {
    private int id; // 记录ID
    private int projectId; // 项目ID
    private String projectName; // 项目名称
    private int quantity; // 生产数量
    private int beforeQuantity; // 更新前数量
    private int afterQuantity; // 更新后数量
    private int operatorId; // 操作人ID
    private String operatorName; // 操作人姓名
    private String remark; // 备注
    private LocalDateTime operationTime; // 操作时间
    private LocalDateTime createTime; // 创建时间
}