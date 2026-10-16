-- 销售订单表
CREATE TABLE IF NOT EXISTS `sale_order` (
  `id` INT PRIMARY KEY COMMENT '订单ID，外部生成',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单编号，如：SO12345',
  `customer_id` INT NOT NULL COMMENT '客户ID',
  `customer_name` VARCHAR(100) NOT NULL COMMENT '客户名称',
  `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
  `quantity` INT NOT NULL COMMENT '数量',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位（个、件、kg等）',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `status` INT NOT NULL DEFAULT 0 COMMENT '订单状态（0:待处理, 1:已确认, 2:已发货, 3:已完成, 4:已取消）',
  `delivery_time` DATETIME DEFAULT NULL COMMENT '交货时间',
  `shipping_time` DATETIME DEFAULT NULL COMMENT '发货时间',
  `operator_id` INT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY `uk_order_no` (`order_no`) COMMENT '订单编号唯一索引',
  KEY `idx_customer_id` (`customer_id`) COMMENT '客户ID索引',
  KEY `idx_status` (`status`) COMMENT '订单状态索引',
  KEY `idx_delivery_time` (`delivery_time`) COMMENT '交货时间索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售订单表';