package com.xm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 库存操作记录视图对象
 */
@Data
public class SimpleStockOperationVO {
    private int id; // 操作记录ID
    private String operationNo; // 操作编号
    // 物料唯一性只由 materialName 保证
    private String materialName; // 物料名称（唯一）

//    private String unit; // 单位
//    private int stockId; // 库存ID
//    private int operationType; // 操作类型（0:入库, 1:出库, 2:库存调整）
    private String operationTypeName; // 操作类型名称
    private int quantity; // 操作数量

//    private int beforeQuantity; // 操作前库存数量
//    private int afterQuantity; // 操作后库存数量
//    private int operatorId; // 操作人ID
//    private String operatorName; // 操作人姓名
//    private String relatedOrderNo; // 关联单据编号
//    private String remark; // 备注
//    private LocalDateTime operationTime; // 操作时间
//    private LocalDateTime createTime; // 创建时间
}