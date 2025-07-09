-- 创建员工请假表
CREATE TABLE `employee_leave` (
  `id` int NOT NULL COMMENT '请假记录ID',
  `employee_id` int NOT NULL COMMENT '员工ID',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `leave_type` varchar(10) NOT NULL COMMENT '请假类型（1:事假, 2:病假, 3:年假, 4:调休, 5:婚假, 6:产假, 7:丧假）',
  `start_time` datetime NOT NULL COMMENT '请假开始时间',
  `end_time` datetime NOT NULL COMMENT '请假结束时间',
  `reason` varchar(500) DEFAULT NULL COMMENT '请假原因',
  `status` int NOT NULL DEFAULT '0' COMMENT '请假状态（0:待审核, 1:已批准, 2:已拒绝, 3:已取消）',
  `reviewer_name` varchar(50) DEFAULT NULL COMMENT '审核人姓名',
  `review_comment` varchar(500) DEFAULT NULL COMMENT '审核意见',
  `review_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_employee_id` (`employee_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='员工请假表'; 