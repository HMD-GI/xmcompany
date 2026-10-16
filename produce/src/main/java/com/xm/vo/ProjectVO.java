package com.xm.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 生产项目视图对象
 */
@Data
public class ProjectVO {
    private int id; // 项目ID
    private String projectName; // 项目名称
    private String status; // 项目状态
    private int currentQuantity; // 当前生产数量
    private int targetQuantity; // 目标生产数量
    private String productName; // 产品名称
//    private String unit; // 单位
//    private LocalDateTime startTime; // 开始时间
//    private LocalDateTime expectedEndTime; // 预计结束时间
//    private LocalDateTime actualEndTime; // 实际结束时间
//    private String remark; // 备注
//    private LocalDateTime createTime; // 创建时间
//    private LocalDateTime updateTime; // 更新时间
    private double progressRate; // 进度百分比
}