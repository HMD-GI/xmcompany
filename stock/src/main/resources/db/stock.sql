-- 库存表
CREATE TABLE IF NOT EXISTS `stock` (
  `id` INT PRIMARY KEY COMMENT '库存记录ID，外部生成',
  -- 已删除 materialId 字段，物料唯一性由 material_name+unit 保证
  `material_name` VARCHAR(100) NOT NULL COMMENT '物料名称',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位（个、件、kg等）',
  -- 已删除 warehouse_location 字段，仓库信息如需扩展可单独设计仓库表
  `quantity` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `last_stock_in_time` DATETIME DEFAULT NULL COMMENT '最后入库时间',
  `last_stock_out_time` DATETIME DEFAULT NULL COMMENT '最后出库时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `uk_material` (`material_name`) COMMENT '物料名称唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表'; 