-- 库存操作记录表
CREATE TABLE IF NOT EXISTS `stock_operation` (
  `id` INT PRIMARY KEY COMMENT '操作记录ID，外部生成',
  `operation_no` VARCHAR(50) NOT NULL COMMENT '操作编号，如：IN12345, OUT12345',
  `stock_id` INT NOT NULL COMMENT '库存ID',
  `operation_type` INT NOT NULL COMMENT '操作类型（0:入库, 1:出库, 2:库存调整）',
  `quantity` INT NOT NULL COMMENT '操作数量',
  `before_quantity` INT NOT NULL COMMENT '操作前库存数量',
  `after_quantity` INT NOT NULL COMMENT '操作后库存数量',
  `operator_id` INT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `operation_time` DATETIME NOT NULL COMMENT '操作时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY `idx_operation_no` (`operation_no`) COMMENT '操作编号索引',
  KEY `idx_stock_id` (`stock_id`) COMMENT '库存ID索引',
  KEY `idx_operation_type` (`operation_type`) COMMENT '操作类型索引',
  KEY `idx_operator_id` (`operator_id`) COMMENT '操作人ID索引',
  KEY `idx_operation_time` (`operation_time`) COMMENT '操作时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存操作记录表'; 