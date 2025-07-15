-- 采购订单表
CREATE TABLE IF NOT EXISTS `purchase_order` (
  `id` INT PRIMARY KEY COMMENT '采购订单ID，外部生成',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号，业务编号',
  `supplier_id` INT NOT NULL COMMENT '供应商ID',
  `item_name` VARCHAR(100) NOT NULL COMMENT '物料名称',
  `unit_price` DECIMAL(12,2) NOT NULL COMMENT '单价',
  `quantity` INT NOT NULL COMMENT '采购数量',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '总金额',
  `status` INT NOT NULL DEFAULT 0 COMMENT '采购订单状态（0:待确认, 1:已确认, 2:已下单, 3:已到货, 4:已完成, 5:已作废）',
  `purchase_request_id` INT COMMENT '关联的采购申请ID',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_supplier_id` (`supplier_id`) COMMENT '供应商ID索引',
  KEY `idx_purchase_request_id` (`purchase_request_id`) COMMENT '采购申请ID索引',
  KEY `idx_order_no` (`order_no`) COMMENT '订单编号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购订单表';