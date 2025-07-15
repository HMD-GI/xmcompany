-- 采购申请表
CREATE TABLE IF NOT EXISTS `purchase_request` (
  `id` INT PRIMARY KEY COMMENT '采购申请ID，外部生成',
  `applicant` VARCHAR(50) NOT NULL COMMENT '申请人',
  `item_name` VARCHAR(100) NOT NULL COMMENT '物料名称',
  `quantity` INT NOT NULL COMMENT '采购数量',
  `budget` DECIMAL(12,2) NOT NULL COMMENT '预算金额',
  `status` INT NOT NULL DEFAULT 0 COMMENT '采购申请状态（0:草稿, 1:审批中, 2:已通过, 3:已驳回, 4:已撤回, 5:已完成）',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='采购申请表'; 