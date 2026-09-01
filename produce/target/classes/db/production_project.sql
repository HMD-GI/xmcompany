-- 生产项目表
CREATE TABLE IF NOT EXISTS `production_project` (
  `id` INT PRIMARY KEY COMMENT '项目ID，外部生成',
  `project_name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `status` VARCHAR(20) NOT NULL DEFAULT '进行中' COMMENT '项目状态（进行中、暂停、已完成、已取消）',
  `current_quantity` INT NOT NULL DEFAULT 0 COMMENT '当前生产数量',
  `target_quantity` INT NOT NULL COMMENT '目标生产数量',
  `product_name` VARCHAR(100) NOT NULL COMMENT '产品名称',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位（个、件、kg等）',
  `start_time` DATETIME NOT NULL COMMENT '开始时间',
  `expected_end_time` DATETIME NOT NULL COMMENT '预计结束时间',
  `actual_end_time` DATETIME DEFAULT NULL COMMENT '实际结束时间',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY `idx_status` (`status`) COMMENT '状态索引',
  KEY `idx_start_time` (`start_time`) COMMENT '开始时间索引',
  KEY `idx_product_name` (`product_name`) COMMENT '产品名称索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产项目表';

-- 生产进度记录表
CREATE TABLE IF NOT EXISTS `production_progress` (
  `id` INT PRIMARY KEY COMMENT '记录ID，外部生成',
  `project_id` INT NOT NULL COMMENT '项目ID',
  `quantity` INT NOT NULL COMMENT '生产数量',
  `before_quantity` INT NOT NULL COMMENT '更新前数量',
  `after_quantity` INT NOT NULL COMMENT '更新后数量',
  `operator_id` INT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `operation_time` DATETIME NOT NULL COMMENT '操作时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY `idx_project_id` (`project_id`) COMMENT '项目ID索引',
  KEY `idx_operation_time` (`operation_time`) COMMENT '操作时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生产进度记录表';