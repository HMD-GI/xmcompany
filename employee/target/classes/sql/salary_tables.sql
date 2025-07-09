-- 薪资配置表
CREATE TABLE IF NOT EXISTS `employee_salary` (
  `id` int NOT NULL COMMENT '薪资配置ID',
  `employee_id` int NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `bank_card_no` varchar(50) NOT NULL COMMENT '银行卡号',
  `bank_name` varchar(100) NOT NULL COMMENT '开户银行',
  `basic_salary` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '基本工资',
  `performance_base` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '绩效基数',
  `allowance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '固定补贴',
  `insurance_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '五险一金金额',
  `effective_date` datetime NOT NULL COMMENT '生效日期',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（1:有效, 0:无效）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工薪资配置表';

-- 薪资单表
CREATE TABLE IF NOT EXISTS `employee_payroll` (
  `id` int NOT NULL COMMENT '薪资单ID',
  `employee_id` int NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `bank_card_no` varchar(50) NOT NULL COMMENT '银行卡号',
  `bank_name` varchar(100) NOT NULL COMMENT '开户银行',
  `payroll_month` varchar(7) NOT NULL COMMENT '薪资月份（格式：yyyy-MM）',
  `basic_salary` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '基本工资',
  `performance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '绩效工资',
  `allowance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '补贴',
  `overtime` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '加班费',
  `bonus` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '奖金',
  `deduction` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '扣款',
  `insurance_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '五险一金',
  `taxable_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '应纳税额',
  `tax` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '个人所得税',
  `actual_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '实发金额',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0:待发放, 1:已发放, 2:已撤销）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `pay_time` datetime DEFAULT NULL COMMENT '发放时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_month` (`employee_id`, `payroll_month`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_payroll_month` (`payroll_month`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='员工薪资单表';

-- 示例数据插入（可选）
INSERT INTO `employee_salary` (`id`, `employee_id`, `employee_name`, `bank_card_no`, `bank_name`, `basic_salary`, `performance_base`, `allowance`, `insurance_amount`, `effective_date`, `status`, `remark`, `create_time`, `update_time`)
VALUES
(1001, 1, '张三', '6225887744556677', '中国建设银行', 8000.00, 3000.00, 1000.00, 1500.00, NOW(), 1, '初始薪资配置', NOW(), NOW()),
(1002, 2, '李四', '6225997755446688', '中国工商银行', 7500.00, 2500.00, 1200.00, 1400.00, NOW(), 1, '初始薪资配置', NOW(), NOW());

-- 添加Redis序列配置
INSERT INTO `redis_key_config` (`key_name`, `current_value`, `step`, `description`, `create_time`, `update_time`)
VALUES
('salary', 1000, 1, '薪资配置ID', NOW(), NOW()),
('payroll', 10000, 1, '薪资单ID', NOW(), NOW()); 